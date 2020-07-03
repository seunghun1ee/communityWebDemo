package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    PostService postService;

    @GetMapping("/posts")
    public String showAllPosts(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts",posts);
        return "postList";
    }

    @GetMapping("/posts/{id}")
    public String showPostById(@PathVariable Long id, Model model) throws Exception {
        Optional<Post> optionalPost = postService.getPostById(id);
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
        postService.addPost(newPost);
        return new RedirectView("/posts");
    }
}
