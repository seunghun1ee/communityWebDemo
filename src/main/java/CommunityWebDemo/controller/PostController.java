package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping("/posts")
    public String showAllPosts(Model model) {
        List<Post> posts = postService.getAll();
        model.addAttribute("posts",posts);
        return "postList";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model model) throws Exception {
        Optional<Post> optionalPost = postService.getById(id);
        Post post;
        if(optionalPost.isPresent()) {
            post = optionalPost.get();
        }
        else throw new Exception();

        model.addAttribute("post",post);
        return "post";
    }

    @GetMapping("/posts/new_post")
    public String newPost() {
        return "newPost";
    }

    @PostMapping("/posts/new_post")
    public RedirectView saveNewPost(Post newPost) {
        postService.add(newPost);
        return new RedirectView("/posts");
    }

    @PostMapping("/posts/{id}/delete")
    public RedirectView delete(@PathVariable Long id) {
        if(postService.deleteById(id)) {
            return new RedirectView("/posts");
        }
        else return new RedirectView("/error");
    }

    @GetMapping("/posts/{id}/edit")
    public String updatePost(@PathVariable Long id, Model model) throws Exception{
        Optional<Post> optionalPost = postService.getById(id);
        Post post;
        if(optionalPost.isPresent()) {
            post = optionalPost.get();
            model.addAttribute("post", post);
            return "update";
        }
        else throw new Exception();
    }

    @PostMapping("/posts/{id}/edit")
    public RedirectView saveUpdatedPost(@PathVariable Long id, Post post) {
        post.setId(id);
        postService.add(post);
        return new RedirectView("/posts/{id}");
    }

}
