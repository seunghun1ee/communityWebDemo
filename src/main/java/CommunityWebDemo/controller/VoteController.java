package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("{threadUrl}/posts/{id}/vote/{type}")
    public String saveVote(@PathVariable String threadUrl, @PathVariable Long id, @PathVariable String type, HttpServletRequest request) throws JSONException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Integer vote = post.getVote();
            JSONArray votingList = new JSONArray(post.getVoterList());
            for(int i = 0; i < votingList.length(); i++) {
                if(votingList.getString(i).equals(request.getRemoteAddr())) {
                    return "already voted";
                }
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

            votingList.put(request.getRemoteAddr());
            String stringVoteList = votingList.toString();
            post.setVoterList(stringVoteList);
            postService.add(post);
            return "success";
        }
        return "failed";
    }
}
