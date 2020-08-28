package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CommentController commentController;

    @GetMapping("/users/{id}")
    public String showUser(@PathVariable Long id, Model model) throws ResponseStatusException {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            List<Post> posts = postService.getPostsOfUser(optionalUser.get());
            model.addAttribute("user",optionalUser.get());
            model.addAttribute("posts",posts);
            //Check if current user is registered or anonymous
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User currentUser = (User) auth.getPrincipal();
                model.addAttribute("currentUser",currentUser);
            }
            else {
                model.addAttribute("currentUser",null);
            }
            return "user";
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    @GetMapping("/users/new_user")
    public String newUser() {
        return "newUser";
    }

    @PostMapping("/users/new_user")
    public RedirectView saveNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.add(user);
        return new RedirectView("/login");
    }

    @PostMapping("/users/{id}/delete")
    public RedirectView deleteUser(@PathVariable Long id) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //is this profile of current user?
            if(optionalUser.get().equals(auth.getPrincipal())) {
                List<Post> posts = postService.getPostsOfUser(optionalUser.get());
                List<Comment> allComments = commentService.getAll();
                List<Comment> commentsFromUser = new ArrayList<>();
                List<Comment> commentsFromPosts = new ArrayList<>();
                //All comments from the user
                for(Comment comment : allComments) {
                    if(comment.getUser().equals(optionalUser.get())) {
                        commentsFromUser.add(comment);
                    }
                }
                //All comments from posts that user made
                for(Post post : posts) {
                    commentsFromPosts.addAll(commentService.getCommentsOfPost(post));
                }
                for(Comment comment : commentsFromUser) {
                    commentController.emptyComment(commentService,comment);
                }
                commentService.deleteAll(commentsFromPosts);
                postService.deleteAll(posts);
                userService.deleteById(id);
                return new RedirectView("/logout");
            }
            //no
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @GetMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, Model model) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //is this profile of current user?
            if(optionalUser.get().equals(auth.getPrincipal())) {
                model.addAttribute("user",optionalUser.get());
                return "updateUser";
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found");
    }

    @PostMapping("/users/{id}/changeUsername")
    public RedirectView saveNewUsername(@PathVariable Long id, String username, String password, RedirectAttributes redirectAttr) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            //is this profile of current user?
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(optionalUser.get().equals(auth.getPrincipal())) {
                User targetUser = optionalUser.get();
                if(passwordEncoder.matches(password,targetUser.getPassword())) {
                    targetUser.setUsername(username);
                    userService.add(targetUser);
                    redirectAttr.addFlashAttribute("successMessage","Username change success");
                    return new RedirectView("/users/{id}");
                }
                else {
                    redirectAttr.addFlashAttribute("failMessage","Wrong password");
                    return new RedirectView("/users/{id}/edit");
                }
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @PostMapping("/users/{id}/changePassword")
    public RedirectView saveNewPassword(@PathVariable Long id, String currentPassword, String newPassword, String repeatPassword, RedirectAttributes redirectAttr) throws ResponseStatusException {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(optionalUser.get().equals(auth.getPrincipal())) {
                User targetUser = optionalUser.get();
                //correct current password?
                if(passwordEncoder.matches(currentPassword,targetUser.getPassword())) {
                    //newPassword == repeatPassword?
                    if(newPassword.equals(repeatPassword)) {
                        targetUser.setPassword(passwordEncoder.encode(newPassword));
                        userService.add(targetUser);
                        redirectAttr.addFlashAttribute("successMessage","Password change success");
                        return new RedirectView("/users/{id}");
                    }
                    else {
                        redirectAttr.addFlashAttribute("failMessage","New passwords do not match");
                        return new RedirectView("/users/{id}/edit");
                    }
                }
                else {
                    redirectAttr.addFlashAttribute("failMessage","Wrong password");
                    return new RedirectView("/users/{id}/edit");
                }
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }
}
