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
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class CommentController {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    //temp
    User testUser = new User("tester");

    @PostMapping("/posts/{postId}/new_comment")
    public RedirectView addComment(@PathVariable Long postId, Comment comment) {
        Optional<Post> optionalPost = postService.getById(postId);
        if(optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comment.setPost(post);
            //temp
            userService.add(testUser);
            comment.setUser(testUser);
            commentService.add(comment);
            return new RedirectView("/posts/{postId}");
        }
        else return new RedirectView("/error");
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, Model model) {
        Optional<Comment> optionalComment = commentService.getById(commentId);
        if(postService.getById(postId).isPresent() && optionalComment.isPresent()) {
            model.addAttribute("comment",optionalComment.get());
            return "updateComment";
        }
        return "error";
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/edit")
    public RedirectView saveEditedComment(@PathVariable Long postId, @PathVariable Long commentId, Comment comment) {
        Optional<Comment> optionalComment = commentService.getById(commentId);
        if(postService.getById(postId).isPresent() && optionalComment.isPresent()) {
            Comment oldComment = optionalComment.get();
            comment.setId(oldComment.getId());
            comment.setUser(oldComment.getUser());
            comment.setPost(oldComment.getPost());
            commentService.add(comment);
            return new RedirectView("/posts/{postId}");
        }
        else return new RedirectView("/error");
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public RedirectView deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        if(postService.getById(postId).isPresent() && commentService.getById(commentId).isPresent()) {
            commentService.deleteById(commentId);
            return new RedirectView("/posts/{postId}");
        }
        else return new RedirectView("/error");
    }
}
