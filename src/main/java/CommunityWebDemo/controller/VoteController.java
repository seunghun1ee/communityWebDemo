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
    public boolean checkVoteBefore(@PathVariable String threadUrl, @PathVariable Long id, @RequestParam boolean isUpvote, HttpServletRequest request) throws JSONException, ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            JSONObject voterObject = new JSONObject(optionalPost.get().getVoterList());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            JSONObject voters = getVoters(voterObject,auth);
            String key = getKey(request,auth);

            if(voters.isNull(key)) {
                return false;
            }
            else {
                return isUpvote == voters.getBoolean(key);
            }
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @PostMapping("/{threadUrl}/posts/{id}/cancelVote")
    public String cancelVote(@PathVariable String threadUrl, @PathVariable Long id, HttpServletRequest request) throws ResponseStatusException, JSONException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            JSONObject voterObject = new JSONObject(optionalPost.get().getVoterList());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            JSONObject voters = getVoters(voterObject,auth);
            String key = getKey(request,auth);

            if(voters.isNull(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"The user did not vote before");
            }
            else {
                //cancel upvote
                if(voters.getBoolean(key)) {
                    post.setVote(post.getVote() - 1);
                }
                //cancel downvote
                else {
                    post.setVote(post.getVote() + 1);
                }
                voters.remove(key);
            }
            String stringVoterObject = voterObject.toString();
            post.setVoterList(stringVoterObject);
            postService.add(post);
            return "success";
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    private JSONObject getVoters(JSONObject voterObject, Authentication auth) throws JSONException {
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return voterObject.getJSONObject("guests");
        }
        else {
            return voterObject.getJSONObject("users");
        }
    }

    private String getKey(HttpServletRequest request, Authentication auth) {
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return request.getRemoteAddr();
        }
        else {
            User currentUser = (User) auth.getPrincipal();
            return String.valueOf(currentUser.getId());
        }
    }
}
