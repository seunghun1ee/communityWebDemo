package CommunityWebDemo.controller;

import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/users")
    public @ResponseBody List<User> showUserList() {
        return userService.getAll();
    }

    @GetMapping("/users/{id}")
    public @ResponseBody User showUser(@PathVariable Long id) throws Exception{
        if(userService.getById(id).isPresent()) {
            return userService.getById(id).get();
        }
        else throw new Exception();
    }
}
