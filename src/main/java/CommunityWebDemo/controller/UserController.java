package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @GetMapping("/users")
    public @ResponseBody List<User> showUserList() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public String showUser(@PathVariable Long id, Model model) throws Exception{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            List<Post> posts = postService.findPostsOfUser(optionalUser.get());
            model.addAttribute("user",optionalUser.get());
            model.addAttribute("posts",posts);
            return "user";
        }
        else throw new Exception();
    }

    @GetMapping("/users/new_user")
    public String newUser() {
        return "newUser";
    }

    @PostMapping("/users/new_user")
    public RedirectView saveNewUser(User user) {
        userService.add(user);
        return new RedirectView("/users");
    }

    @PostMapping("/users/{id}/delete")
    public RedirectView deleteUser(@PathVariable Long id) {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            List<Post> posts = postService.findPostsOfUser(optionalUser.get());

            List<Comment> comments = new ArrayList<>();
            for(Post post : posts) {
                comments.addAll(commentService.getCommentsOfPost(post));
            }
            commentService.deleteAll(comments);
            postService.deleteAll(posts);
            userService.deleteById(id);
            return new RedirectView("/users");
        }
        else return new RedirectView("/error");
    }

    @GetMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            model.addAttribute("user",optionalUser.get());
            return "updateUser";
        }
        else return "error";
    }

    @PostMapping("/users/{id}/edit")
    public RedirectView saveUpdatedUser(@PathVariable Long id, User user) {
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            User oldUser = optionalUser.get();
            user.setId(oldUser.getId());
            userService.add(user);
            return new RedirectView("/users/{id}");
        }
        else return new RedirectView("/error");
    }
}
