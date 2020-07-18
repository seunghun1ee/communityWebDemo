package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.CommentRepository;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/")
    public String helloWorld() {
        return "home";
    }

    @GetMapping("/load")
    public @ResponseBody String loadTestData() {
        commentService.deleteAll();
        postService.deleteAll();
        userService.deleteAll();
        threadRepository.deleteAll();
        User adam = new User("Adam");
        User eve = new User("Eve");
        userService.add(adam);
        userService.add(eve);

        Thread threadA = new Thread("a","Thread A");
        Thread threadB = new Thread("b","Thread B");
        threadRepository.save(threadA);
        threadRepository.save(threadB);
//        List<User> users = userService.getAll();
//        for(User user : users) {
//            switch (user.getName()) {
//                case "Adam":
//                    adam = user;
//                case "Eve":
//                    eve = user;
//                default:
//            }
//        }
        postService.add(new Post(threadA,"first post","hello", adam));
        postService.add(new Post(threadA,"second post", "nice to meet you", eve));
        postService.add(new Post(threadA,"third post","this is adam", adam));
        postService.add(new Post(threadB,"first of /b","hello /b", eve));

        List<Post> posts = postService.getAll();
        commentService.add(new Comment(posts.get(0),eve,"hi"));

        return "Test data is loaded";
    }
}
