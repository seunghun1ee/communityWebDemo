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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    @Autowired
    PasswordEncoder passwordEncoder;

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

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //Post author Anonymous or Registered
            if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                newPost.setIp(ipHandler.trimIpAddress(request.getRemoteAddr()));
                newPost.setPassword(passwordEncoder.encode(newPost.getPassword()));
            }
            else {
                newPost.setUser((User) auth.getPrincipal());
            }
        }
        else throw new Exception();

        postService.add(newPost);
        return new RedirectView("/{threadInitial}/posts");
    }

    @PostMapping("/{threadUrl}/posts/{id}/delete")
    public RedirectView delete(@PathVariable String threadUrl, @PathVariable Long id, String password, RedirectAttributes redirectAttr) {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        //The thread is present, the post is present and the thread of the post is same
        if(optionalThread.isPresent() && optionalPost.isPresent() && optionalPost.get().getThread().equals(optionalThread.get())) {
            Post post = optionalPost.get();
            //Owner of the post Anonymous or Registered?
            if(post.getUser() == null) {
                //password match?
                if(passwordEncoder.matches(password, post.getPassword())) {
                    List<Comment> comments = commentService.getCommentsOfPost(optionalPost.get());
                    commentService.deleteAll(comments);
                    postService.deleteById(id);
                    redirectAttr.addFlashAttribute("successMessage","The post is deleted");
                    return new RedirectView("/{threadUrl}/posts");
                }
                //wrong password
                else {
                    redirectAttr.addFlashAttribute("failMessage","Wrong password");
                    return new RedirectView("/{threadUrl}/posts/{id}");
                }
            }
            //Registered user
            else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                //Is current user the owner of the post?
                if(post.getUser().equals(auth.getPrincipal())) {
                    List<Comment> comments = commentService.getCommentsOfPost(optionalPost.get());
                    commentService.deleteAll(comments);
                    postService.deleteById(id);
                    redirectAttr.addFlashAttribute("successMessage","The post is deleted");
                    return new RedirectView("/{threadUrl}/posts");
                }
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access Denied");
                    return new RedirectView("/{threadUrl}/posts/{id}");
                }
            }
        }
        return new RedirectView("/error");
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
