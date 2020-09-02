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
import org.springframework.http.HttpStatus;
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
    public boolean saveNewComment(@PathVariable String threadUrl, @PathVariable Long postId, @RequestBody String payload, HttpServletRequest request) throws JSONException, ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(postId);
        if(!optionalThread.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no_thread");
        }
        if(optionalPost.isPresent()) {
            JSONObject commentJson = new JSONObject(payload);
            Comment comment = parseJsonToComment(commentJson);
            setupCommentUser(comment,request);
            comment.setPost(optionalPost.get());
            commentService.add(comment);
            return true;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_post");
    }

    @PostMapping(value = "/{threadUrl}/posts/{postId}/comments/{commentId}/delete", consumes = "application/json")
    @ResponseBody
    public boolean deleteComment(@PathVariable String threadUrl,@PathVariable Long postId, @PathVariable Long commentId, @RequestBody String payload, RedirectAttributes redirectAttr) throws JSONException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(!optionalThread.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_thread");
        }
        Optional<Post> optionalPost = postService.getById(postId);
        if(!optionalPost.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_post");
        }
        Comment comment = getActiveCommentOrException(commentService.getById(commentId));

        JSONObject passwordJson = new JSONObject(payload);
        String password = passwordJson.optString("password");
        //anonymous or registered?
        if(comment.getUser() == null) {
            //Comment password is correct
            if(passwordEncoder.matches(password, comment.getPassword())) {
                emptyComment(commentService,comment);
            }
            else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Wrong password");
            }
        }
        else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User authUser = (User) auth.getPrincipal();
            //Current logged in user is the owner of the comment
            if(comment.getUser().equals(authUser)) {
                emptyComment(commentService,comment);
            }
            else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
            }
        }
        return true;
    }

    @PostMapping("/{threadUrl}/posts/{postId}/comments/{commentId}/reply")
    @ResponseBody
    public boolean saveNewReply(@PathVariable String threadUrl, @PathVariable Long postId, @PathVariable Long commentId, @RequestBody String payload, HttpServletRequest request) throws JSONException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(!optionalThread.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_thread");
        }
        Post post = getPostOrException(postService.getById(postId));
        Comment parentComment = getActiveCommentOrException(commentService.getById(commentId));

        JSONObject commentJson = new JSONObject(payload);
        Comment comment = parseJsonToComment(commentJson);
        setupCommentUser(comment,request);
        comment.setParentComment(parentComment);
        comment.setPost(post);
        commentService.add(comment);
        return true;
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

    private Comment getActiveCommentOrException(Optional<Comment> optionalComment) {
        if(!optionalComment.isPresent() || !optionalComment.get().isActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_comment");
        }
        else return optionalComment.get();
    }

    private Post getPostOrException(Optional<Post> optionalPost) {
        if(!optionalPost.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"no_post");
        }
        else return optionalPost.get();
    }

}
