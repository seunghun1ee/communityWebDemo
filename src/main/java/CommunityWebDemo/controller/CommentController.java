package CommunityWebDemo.controller;

import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CommentController {

    @Autowired
    CommentRepository commentRepository;

    User testUser = new User("tester");


}
