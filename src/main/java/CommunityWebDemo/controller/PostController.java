package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping("/posts")
    public @ResponseBody List<Post> showAllPosts() {
        postService.addPost(new Post("first post","hello",0L));
        postService.addPost(new Post("second post", "nice to meet you",1L));

        return postService.getAllPosts();
    }
}
