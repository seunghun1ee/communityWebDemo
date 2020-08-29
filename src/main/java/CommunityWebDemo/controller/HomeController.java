package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String helloWorld(Model model) {
        List<Thread> threads = (List<Thread>) threadRepository.findAll();
        model.addAttribute("threads",threads);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            User authUser = (User) auth.getPrincipal();
            Optional<User> loginUser = userService.getById(authUser.getId());
            if(loginUser.isPresent()) {
                model.addAttribute("user",loginUser.get());
            }
            else return "redirect:/logout";
        }
        return "home";
    }

    @GetMapping("/load")
    public @ResponseBody String loadTestData() {
        commentService.deleteAll();
        postService.deleteAll();
        userService.deleteAll();
        threadRepository.deleteAll();
        User adam = new User("adam",passwordEncoder.encode("1234"));
        User eve = new User("eve",passwordEncoder.encode("1234"));
        userService.add(adam);
        userService.add(eve);

        Thread threadA = new Thread("a","Thread A");
        Thread threadB = new Thread("b","Thread B");
        threadRepository.save(threadA);
        threadRepository.save(threadB);

        postService.add(new Post(threadA,"first post","hello", adam));
        postService.add(new Post(threadA,"second post", "nice to meet you", eve));
        postService.add(new Post(threadA,"third post","this is adam", adam));
        postService.add(new Post(threadB,"first of /b","hello /b", eve));

        List<Post> posts = postService.getAll();
        commentService.add(new Comment(posts.get(0),eve,"hi"));
        Comment parent = new Comment(posts.get(0),adam,"reply me");
        Comment child = new Comment(posts.get(0),eve,"reply");
        commentService.add(parent);
        commentService.add(child);

        return "Test data is loaded";
    }
}
