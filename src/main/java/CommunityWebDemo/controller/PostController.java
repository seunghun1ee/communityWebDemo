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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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

    @GetMapping("/{threadUrl}/posts")
    public String showAllPostsOfThread(@PathVariable String threadUrl,Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            List<Post> posts = postService.getPostsOfThread(optionalThread.get());
            model.addAttribute("thread",optionalThread.get());
            model.addAttribute("posts",posts);
            return "postList";
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    @GetMapping("/{threadUrl}/posts/{id}")
    public String showPostById(@PathVariable String threadUrl, @PathVariable Long id, Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            List<Comment> comments = commentService.getCommentsOfPost(post);
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
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");

        return "post";
    }

    @GetMapping("/{threadUrl}/new_post")
    public String newPost(@PathVariable String threadUrl, Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            model.addAttribute("thread",optionalThread.get());
            return "newPost";
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");

    }

    @PostMapping("/{threadUrl}/new_post")
    public RedirectView saveNewPost(@PathVariable String threadUrl, Post newPost, HttpServletRequest request) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
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
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");

        postService.add(newPost);
        return new RedirectView("/{threadUrl}/posts");
    }

    @PostMapping("/{threadUrl}/posts/{id}/delete")
    public RedirectView delete(@PathVariable String threadUrl, @PathVariable Long id, String password, RedirectAttributes redirectAttr) throws ResponseStatusException{
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
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @GetMapping("/{threadUrl}/posts/{id}/edit")
    public String updatePost(@PathVariable String threadUrl, @PathVariable Long id, Model model, RedirectAttributes redirectAttr) throws ResponseStatusException{
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        //The thread is present, the post is present
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //is this post owned by registered user?
            if(optionalPost.get().getUser() != null) {
                //current user is the owner of the post
                if(optionalPost.get().getUser().equals(auth.getPrincipal())) {
                    model.addAttribute("thread",optionalThread.get());
                    model.addAttribute("post", post);
                    return "updatePost";
                }
                //current user is not the owner of the post
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access denied");
                    return "redirect:/{threadUrl}/posts/{id}";
                }
            }
            //This post was written by anonymous user
            else {
                model.addAttribute("thread",optionalThread.get());
                model.addAttribute("post", post);
                return "updatePost";
            }

        }
        //thread or post is not present, throw exception
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    @PostMapping("/{threadUrl}/posts/{id}/edit")
    public RedirectView saveUpdatedPost(@PathVariable String threadUrl,@PathVariable Long id, Post post,RedirectAttributes redirectAttr) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(id);
        //The thread and the post are present
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            //apply changes
            Post targetPost = optionalPost.get();
            targetPost.setTitle(post.getTitle());
            targetPost.setBody(post.getBody());
            //the owner of the post registered or anonymous?
            if(targetPost.getUser() != null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                //current user is the owner of the post
                if(targetPost.getUser().equals(auth.getPrincipal())) {
                    postService.add(targetPost);
                }
                //wrong user
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access denied");
                }
            }
            //owner of the post is anonymous
            else {
                //check password
                if(passwordEncoder.matches(post.getPassword(),targetPost.getPassword())) {
                    postService.add(targetPost);
                }
                //wrong password
                else {
                    redirectAttr.addFlashAttribute("failMessage","Wrong password");
                    //redirectAttr.addFlashAttribute("post",post);
                    return new RedirectView("/{threadUrl}/posts/{id}/edit");
                }
            }

            return new RedirectView("/{threadUrl}/posts/{id}");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

}
