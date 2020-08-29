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
            JSONObject voters;
            boolean isUpvote;
            switch (type) {
                case "upvote":
                    post.setVote(vote + 1);
                    isUpvote = true;
                    break;
                case "downvote":
                    post.setVote(vote - 1);
                    isUpvote = false;
                    break;
                default:
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
            }
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                voters = voterObject.getJSONObject("guests");
                voters.put(request.getRemoteAddr(),isUpvote);
            }
            else {
                voters = voterObject.getJSONObject("users");
                User currentUser = (User) auth.getPrincipal();
                voters.put(String.valueOf(currentUser.getId()),isUpvote);
            }
            String stringVoterObject = voterObject.toString();
            post.setVoterList(stringVoterObject);
            postService.add(post);
            return "success";
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @PostMapping("/{threadUrl}/posts/{id}/checkVoteBefore")
    public @ResponseBody boolean checkVoteBefore(@PathVariable String threadUrl, @PathVariable Long id, @RequestParam boolean isUpvote, HttpServletRequest request) throws JSONException, ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            JSONObject voterObject = new JSONObject(optionalPost.get().getVoterList());
            JSONObject voters;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //anonymous or registered user?
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                voters = voterObject.getJSONObject("guests");
                if(voters.isNull(request.getRemoteAddr())) {
                    return false;
                }
                else {
                    return isUpvote == voters.getBoolean(request.getRemoteAddr());
                }
            }
            else {
                voters = voterObject.getJSONObject("users");
                User currentUser = (User) auth.getPrincipal();
                if(voters.isNull(String.valueOf(currentUser.getId()))) {
                    return false;
                }
                else {
                    return isUpvote == voters.getBoolean(String.valueOf(currentUser.getId()));
                }
            }
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }
}
