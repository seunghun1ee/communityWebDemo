package CommunityWebDemo.controller;

import CommunityWebDemo.compartor.SortByPostDateTime;
import CommunityWebDemo.compartor.SortByPostVote;
import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import CommunityWebDemo.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CommentController commentController;

    @GetMapping("/")
    public String helloWorld(Model model, @RequestParam(required = false,defaultValue = "vote") String sort) throws JSONException {
        List<Thread> threads = (List<Thread>) threadRepository.findAll();
        List<Post> posts = postService.getAll();
        for(Post post : posts) {
            commentController.setActiveCommentNumber(post);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            User authUser = (User) auth.getPrincipal();
            JSONObject subscribedThreadsJSON = new JSONObject(authUser.getSubscribedThreads());
            List<Thread> subscribedThreads = new ArrayList<>();
            Iterator<String> threadUrls = subscribedThreadsJSON.keys();
            while (threadUrls.hasNext()) {
                String threadUrl = threadUrls.next();
                Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
                optionalThread.ifPresent(subscribedThreads::add);
            }
            model.addAttribute("subscribedThreads",subscribedThreads);
            List<Post> subscribedPosts = new ArrayList<>();
            for(Post post : posts) {
                for(Thread thread : subscribedThreads) {
                    if(post.getThread().equals(thread)) {
                        subscribedPosts.add(post);
                        break;
                    }
                }
            }
            switch (sort) {
                case "date":
                    subscribedPosts.sort(new SortByPostDateTime());
                    break;
                case "vote":
                default:
                    subscribedPosts.sort(new SortByPostVote());
                    break;
            }

            subscribedPosts.sort(new SortByPostVote());
            model.addAttribute("subscribedPosts",subscribedPosts);
        }
        switch (sort) {
            case "date":
                posts.sort(new SortByPostDateTime());
                break;
            case "vote":
            default:
                posts.sort(new SortByPostVote());
                break;
        }
        model.addAttribute("threads",threads);
        model.addAttribute("posts",posts);

        return "home";
    }

}
