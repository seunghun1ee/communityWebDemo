package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PostController {

    @GetMapping("/posts")
    public @ResponseBody List<Post> showAllPosts() {
        List<Post> posts = new ArrayList<>();
        posts.add(new Post(0L,"post0 title","post0 body", (long) 0));
        posts.add(new Post(1L,"post1 title","post1 body", (long) 1));

        return posts;
    }
}
