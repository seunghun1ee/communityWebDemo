package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.CommentRepository;
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
    CommentRepository commentRepository;

    @GetMapping("/")
    public String helloWorld() {
        return "home";
    }

    @GetMapping("/load")
    public @ResponseBody String loadTestData() {
        postService.deleteAll();
        userService.deleteAll();
        User adam = new User("Adam");
        User eve = new User("Eve");
        userService.add(adam);
        userService.add(eve);
        List<User> users = userService.getAll();
        for(User user : users) {
            switch (user.getName()) {
                case "Adam":
                    adam = user;
                case "Eve":
                    eve = user;
                default:
            }
        }
        postService.add(new Post("first post","hello", adam));
        postService.add(new Post("second post", "nice to meet you", eve));
        postService.add(new Post("third post","this is adam",adam));

        List<Post> posts = postService.getAll();
        commentRepository.save(new Comment(posts.get(0),eve,"hi"));

        return "Test data is loaded";
    }
}
