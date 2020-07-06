package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.UserService;
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
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public @ResponseBody List<User> showUserList() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public String showUser(@PathVariable Long id, Model model) throws Exception{
        Optional<User> optionalUser = userService.getById(id);
        if(optionalUser.isPresent()) {
            List<Post> posts = userService.findPostsOfUser(optionalUser.get());
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
}
