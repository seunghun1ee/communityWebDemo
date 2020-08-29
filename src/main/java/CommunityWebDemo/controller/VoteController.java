package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class VoteController {

    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;

    @PostMapping("/{threadUrl}/posts/{id}/vote")
    public String saveVote(@PathVariable String threadUrl, @PathVariable Long id, @RequestParam String type, HttpServletRequest request) throws JSONException, ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Integer vote = post.getVote();
            JSONObject voterObject = new JSONObject(post.getVoterList());
            JSONArray guestVoterList = voterObject.getJSONArray("guests");
            JSONArray userVoterList = voterObject.getJSONArray("users");
            //Current user anonymous or registered?
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //Anonymous
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                guestVoterList.put(request.getRemoteAddr());
            }
            //registered
            else {
                User currentUser = (User) auth.getPrincipal();
                userVoterList.put(currentUser.getId());
            }
            switch (type) {
                case "upvote":
                    post.setVote(vote + 1);
                    break;
                case "downvote":
                    post.setVote(vote - 1);
                    break;
                default:
                    return "failed";
            }
            String stringVoteList = voterObject.toString();
            post.setVoterList(stringVoteList);
            postService.add(post);
            return "success";
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @PostMapping("/{threadUrl}/posts/{id}/checkVoteBefore")
    public @ResponseBody boolean checkVoteBefore(@PathVariable String threadUrl, @PathVariable Long id, HttpServletRequest request) throws JSONException, ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            JSONObject voterObject = new JSONObject(optionalPost.get().getVoterList());
            JSONArray voters;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //anonymous or registered user?
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                voters = voterObject.getJSONArray("guests");
                for(int i=0; i < voters.length(); i++) {
                    if(voters.getString(i).equals(request.getRemoteAddr())) {
                        return true;
                    }
                }
            }
            else {
                voters = voterObject.getJSONArray("users");
                User currentUser = (User) auth.getPrincipal();
                for(int i=0; i < voters.length(); i++) {
                    if(voters.getLong(i) == currentUser.getId()) {
                        return true;
                    }
                }
            }
            return false;
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }
}
