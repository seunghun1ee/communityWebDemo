package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/posts/{id}")
    public @ResponseBody Post showPostById(@PathVariable Long id) throws Exception {
        Optional<Post> optionalPost = postService.getPostById(id);
        Post post;
        if(optionalPost.isPresent()) {
            post = optionalPost.get();
        }
        else throw new Exception();

        return post;
    }
}
