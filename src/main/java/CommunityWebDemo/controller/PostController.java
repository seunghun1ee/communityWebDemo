package CommunityWebDemo.controller;

import CommunityWebDemo.security.IpHandler;
import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadService threadService;

    IpHandler ipHandler = new IpHandler();

    User testUser = new User("tester");

    @GetMapping("/{threadInitial}/posts")
    public String showAllPostsOfThread(@PathVariable String threadInitial,Model model) throws Exception {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        List<Post> posts;
        String threadName;
        if(optionalThread.isPresent()) {
            posts = postService.getPostsOfThread(optionalThread.get());
            model.addAttribute("thread",optionalThread.get());
            model.addAttribute("posts",posts);
            return "postList";
        }
        else throw new Exception();


    }

    @GetMapping("/{threadInitial}/posts/{id}")
    public String showPostById(@PathVariable String threadInitial, @PathVariable Long id, Model model) throws Exception {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if(optionalThread.isPresent()) {
            Optional<Post> optionalPost = postService.getById(id);

            List<Comment> comments;

            Post post;
            if(optionalPost.isPresent()) {
                post = optionalPost.get();
                comments = commentService.getCommentsOfPost(post);
            }
            else throw new Exception();
            model.addAttribute("thread",optionalThread.get());
            model.addAttribute("post",post);
            model.addAttribute("comments",comments);
            //Check if current user is registered or anonymous
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User currentUser = (User) auth.getPrincipal();
                model.addAttribute("currentUser",currentUser);
            }
            else {
                model.addAttribute("currentUser",null);
            }

        }
        else throw new Exception();

        return "post";
    }

    @GetMapping("/{threadInitial}/new_post")
    public String newPost(@PathVariable String threadInitial, Model model) throws Exception {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if(optionalThread.isPresent()) {
            model.addAttribute("thread",optionalThread.get());
            return "newPost";
        }
        else throw new Exception();

    }

    @PostMapping("/{threadInitial}/new_post")
    public RedirectView saveNewPost(@PathVariable String threadInitial, Post newPost, HttpServletRequest request) throws Exception {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if(optionalThread.isPresent()) {
            newPost.setThread(optionalThread.get());
            newPost.setIp(ipHandler.trimIpAddress(request.getRemoteAddr()));
        }
        else throw new Exception();
        //temporary
        //userService.add(testUser);
        //newPost.setUser(testUser);
        postService.add(newPost);
        return new RedirectView("/{threadInitial}/posts");
    }

    @PostMapping("/{threadInitial}/posts/{id}/delete")
    public RedirectView delete(@PathVariable String threadInitial ,@PathVariable Long id, String password) {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if (!optionalThread.isPresent()) {
            return new RedirectView("/error");
        }

        Optional<Post> optionalPost = postService.getById(id);
        if(optionalPost.isPresent() && optionalPost.get().getThread().equals(optionalThread.get())) {
            if(optionalPost.get().getPassword().equals(password)) {
                List<Comment> comments = commentService.getCommentsOfPost(optionalPost.get());
                commentService.deleteAll(comments);
                postService.deleteById(id);
                return new RedirectView("/{threadInitial}/posts");
            }
            else {
                return new RedirectView("/{threadInitial}/posts/{id}");
            }

        }
        else return new RedirectView("/error");
    }

    @GetMapping("/{threadInitial}/posts/{id}/edit")
    public String updatePost(@PathVariable String threadInitial, @PathVariable Long id, Model model) throws Exception{
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if(!optionalThread.isPresent()) {
            throw new Exception();
        }
        Optional<Post> optionalPost = postService.getById(id);
        Post post;
        if(optionalPost.isPresent()) {
            post = optionalPost.get();
            model.addAttribute("thread",optionalThread.get());
            model.addAttribute("post", post);
            return "updatePost";
        }
        else throw new Exception();
    }

    @PostMapping("/{threadInitial}/posts/{id}/edit")
    public RedirectView saveUpdatedPost(@PathVariable String threadInitial,@PathVariable Long id, Post post) {
        Optional<Thread> optionalThread = threadService.getByUrl(threadInitial);
        if(!optionalThread.isPresent()) {
            return new RedirectView("/error");
        }

        Optional<Post> optionalPost = postService.getById(id);
        if(optionalPost.isPresent()) {
            //check password
            if(optionalPost.get().getPassword().equals(post.getPassword())) {
                Post oldPost = optionalPost.get();
                post.setId(oldPost.getId());
                post.setUser(oldPost.getUser());
                post.setIp(oldPost.getIp());
                post.setThread(optionalThread.get());
                postService.add(post);
            }

            return new RedirectView("/{threadInitial}/posts/{id}");
        }
        else return new RedirectView("/error");
    }

}
