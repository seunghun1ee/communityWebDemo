package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import CommunityWebDemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Controller
public class CommentController {

    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadService threadService;
    //temp
    User testUser = new User("tester");

    @PostMapping("/posts/{postId}/new_comment")
    public RedirectView addComment(@PathVariable Long postId, Comment comment, HttpServletRequest request) {
        Optional<Post> optionalPost = postService.getById(postId);
        if(optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comment.setPost(post);
            comment.setIp(trimIpAddress(request.getRemoteAddr()));
            //temp
            userService.add(testUser);
            comment.setUser(testUser);
            commentService.add(comment);
            if(post.getThread() == null) {
                return new RedirectView("/error");
            }
            return new RedirectView("/" + post.getThread().getUrl() + "/posts/{postId}");
        }
        else return new RedirectView("/error");
    }

    @GetMapping("/posts/{postId}/comments/{commentId}/edit")
    public String editComment(@PathVariable Long postId, @PathVariable Long commentId, Model model) {
        Optional<Post> optionalPost = postService.getById(postId);
        Optional<Comment> optionalComment = commentService.getById(commentId);
        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalPost.get().getThread() != null) {
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
    public RedirectView deleteComment(@PathVariable Long postId, @PathVariable Long commentId, String password, RedirectAttributes redirectAttr) {
        Optional<Post> optionalPost = postService.getById(postId);
        Optional<Comment> optionalComment = commentService.getById(commentId);
        //Post is present, comment is present, thread is not null
        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalPost.get().getThread() != null) {
            //Comment password is correct
            if(optionalComment.get().getPassword().equals(password)) {
                commentService.deleteById(commentId);
                redirectAttr.addFlashAttribute("successMessage","The Comment is deleted");
            }
            else {
                redirectAttr.addFlashAttribute("failMessage","Password is incorrect");
            }
            return new RedirectView("/" + optionalPost.get().getThread().getUrl() + "/posts/{postId}");
        }
        else return new RedirectView("/error");
    }

    private String trimIpAddress(String ip) {
        String[] strings = ip.split("\\.");
        return strings[0] + "." + strings[1] + ".***.***";
    }
}
