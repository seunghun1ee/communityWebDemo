package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import org.springframework.web.servlet.ModelAndView;
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
            //If the user is disabled user
            if(!optionalUser.get().isActive()) {
                return "deletedUser";
            }
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
    public ModelAndView saveNewUser(String username, String password, String repeatPassword) {
        ModelAndView modelAndView = new ModelAndView();
        Optional<User> existingUser = userService.getByUsername(username);
        if(existingUser.isPresent()) {
            modelAndView.addObject("failMessage","The username is already in use");
            modelAndView.setViewName("newUser");
            return modelAndView;
        }
        else if(password.equals(repeatPassword)) {
            User user = new User(username,passwordEncoder.encode(password));
            userService.add(user);
            return new ModelAndView("redirect:/login");
        }
        else {
            modelAndView.addObject("failMessage","Passwords don't match");
            modelAndView.setViewName("newUser");
            return modelAndView;
        }
    }

    @PostMapping("/users/{id}/delete")
    public RedirectView deleteUser(@PathVariable Long id) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //is this profile of current user?
                if(optionalUser.get().equals(authUser)) {
                    List<Post> posts = postService.getPostsOfUser(optionalUser.get());
                    List<Comment> allComments = commentService.getAll();
                    List<Comment> commentsFromUser = new ArrayList<>();
                    List<Comment> commentsFromPosts = new ArrayList<>();
                    //All comments from the user
                    for(Comment comment : allComments) {
                        if(comment.getUser() != null && comment.getUser().equals(optionalUser.get())) {
                            commentsFromUser.add(comment);
                        }
                    }
                    //All comments from posts that user made
                    for(Post post : posts) {
                        post.setActive(false);
                        commentsFromPosts.addAll(commentService.getCommentsOfPost(post));
                    }
                    for(Comment comment : commentsFromUser) {
                        commentController.emptyComment(commentService,comment);
                    }
                    commentService.deleteAll(commentsFromPosts);
                    postService.addAll(posts);
                    authUser.setActive(false);
                    userService.add(authUser);
                    return new RedirectView("/logout");
                }
                //no
                else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @GetMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, Model model) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //is this profile of current user?
                if(optionalUser.get().equals(authUser)) {
                    model.addAttribute("user",optionalUser.get());
                    return "updateUser";
                }
                else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page not found");
    }

    @PostMapping("/users/{id}/changeUsername")
    public RedirectView saveNewUsername(@PathVariable Long id, String username, String password, RedirectAttributes redirectAttr) throws ResponseStatusException{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //is this profile of current user?
                if(optionalUser.get().equals(authUser)) {
                    User targetUser = optionalUser.get();
                    if(passwordEncoder.matches(password,targetUser.getPassword())) {
                        targetUser.setUsername(username);
                        userService.add(targetUser);
                        //new auth with new username
                        UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(targetUser,null,auth.getAuthorities());
                        //apply new auth
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
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
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @PostMapping("/users/{id}/changePassword")
    public RedirectView saveNewPassword(@PathVariable Long id, String currentPassword, String newPassword, String repeatPassword, RedirectAttributes redirectAttr) throws ResponseStatusException {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //logged in user == user profile?
                if(optionalUser.get().equals(authUser)) {
                    User targetUser = optionalUser.get();
                    //correct current password?
                    if(passwordEncoder.matches(currentPassword,targetUser.getPassword())) {
                        //newPassword == repeatPassword?
                        if(newPassword.equals(repeatPassword)) {
                            targetUser.setPassword(passwordEncoder.encode(newPassword));
                            userService.add(targetUser);
                            //new auth with new password
                            UsernamePasswordAuthenticationToken usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(targetUser,null,auth.getAuthorities());
                            //apply new auth
                            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
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
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }
}
