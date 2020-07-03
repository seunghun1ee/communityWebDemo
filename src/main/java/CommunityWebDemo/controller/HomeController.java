package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.repository.PostRepository;
import CommunityWebDemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;

    @GetMapping("/")
    public String helloWorld() {
        return "home";
    }

    @GetMapping("/load")
    public @ResponseBody String loadTestData() {
        postRepository.deleteAll();
        postService.addPost(new Post("first post","hello",0L));
        postService.addPost(new Post("second post", "nice to meet you",1L));

        return "Test data is loaded";
    }
}
