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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
public class VoteController {

    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;

    @PostMapping("/{threadUrl}/posts/{id}/vote/{type}")
    public String saveVote(@PathVariable String threadUrl, @PathVariable Long id, @PathVariable String type, HttpServletRequest request) throws JSONException {
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
                for(int i = 0; i < guestVoterList.length(); i++) {
                    if(guestVoterList.getString(i).equals(request.getRemoteAddr())) {
                        return "already voted";
                    }
                }
                guestVoterList.put(request.getRemoteAddr());
            }
            //registered
            else {
                User currentUser = (User) auth.getPrincipal();
                for(int i = 0; i < userVoterList.length(); i++) {
                    if(userVoterList.getLong(i) == currentUser.getId()) {
                        return "already voted";
                    }
                }
                userVoterList.put(currentUser.getId());
            }
            //not voted before
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
        return "failed";
    }
}
