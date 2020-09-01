package CommunityWebDemo.controller;

import CommunityWebDemo.security.IpHandler;
import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import CommunityWebDemo.service.UserService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    PasswordEncoder passwordEncoder;

    IpHandler ipHandler = new IpHandler();

    @PostMapping(value = "/{threadUrl}/posts/{postId}/new_comment", consumes = "application/json")
    @ResponseBody
    public boolean saveNewComment(@PathVariable String threadUrl, @PathVariable Long postId, @RequestBody String payload, HttpServletRequest request) throws JSONException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(postId);
        if(optionalThread.isPresent() && optionalPost.isPresent()) {
            JSONObject commentJson = new JSONObject(payload);
            Comment comment = parseJsonToComment(commentJson);
            setupCommentUser(comment,request);
            comment.setPost(optionalPost.get());
            commentService.add(comment);
            return true;
        }
        return false;
    }

    @PostMapping(value = "/posts/{postId}/comments/{commentId}/delete", consumes = "application/json")
    @ResponseBody
    public boolean deleteComment(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody String payload, RedirectAttributes redirectAttr) throws JSONException {
        Optional<Post> optionalPost = postService.getById(postId);
        Optional<Comment> optionalComment = commentService.getById(commentId);
        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalPost.get().getThread() != null) {
            JSONObject passwordJson = new JSONObject(payload);
            String password = passwordJson.optString("password");
            Comment comment = optionalComment.get();
            //anonymous or registered?
            if(comment.getUser() == null) {
                //Comment password is correct
                if(passwordEncoder.matches(password, comment.getPassword())) {
                    emptyComment(commentService,comment);
                    redirectAttr.addFlashAttribute("successMessage","The Comment is deleted");
                }
                else {
                    redirectAttr.addFlashAttribute("failMessage","Password is incorrect");
                }
            }
            else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User authUser = (User) auth.getPrincipal();
                //Current logged in user is the owner of the comment
                if(comment.getUser().equals(authUser)) {
                    emptyComment(commentService,comment);
                    redirectAttr.addFlashAttribute("successMessage","The Comment is deleted");
                }
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access denied");
                }
            }
            return true;
        }
        else return false;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    @ResponseBody
    public boolean saveNewReply(@PathVariable Long postId, @PathVariable Long commentId, @RequestBody String payload, HttpServletRequest request) throws JSONException {
        Optional<Post> optionalPost = postService.getById(postId);
        Optional<Comment> optionalComment = commentService.getById(commentId);
        if(optionalPost.isPresent() && optionalComment.isPresent()) {
            JSONObject commentJson = new JSONObject(payload);
            Comment comment = parseJsonToComment(commentJson);
            setupCommentUser(comment,request);
            comment.setParentComment(optionalComment.get());
            comment.setPost(optionalPost.get());
            commentService.add(comment);
            return true;
        }
        return false;
    }

    private Comment parseJsonToComment(JSONObject commentJson) {
        Comment comment = new Comment();
        comment.setMessage(commentJson.optString("message"));
        comment.setPassword(commentJson.optString("password"));
        return comment;
    }

    private void setupCommentUser(Comment comment, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Anonymous user or registered user?
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            comment.setIp(ipHandler.trimIpAddress(request.getRemoteAddr()));
            comment.setPassword(passwordEncoder.encode(comment.getPassword()));
        }
        else {
            User authUser = (User) auth.getPrincipal();
            comment.setUser(authUser);
        }
    }

    public void emptyComment(CommentService commentService,Comment comment) {
        comment.setMessage(null);
        comment.setUser(null);
        comment.setActive(false);
        commentService.add(comment);
    }

}
