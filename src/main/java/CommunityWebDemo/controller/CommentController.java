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
    @Autowired
    PasswordEncoder passwordEncoder;

    IpHandler ipHandler = new IpHandler();

//    @PostMapping("/posts/{postId}/new_comment")
//    public RedirectView addComment(@PathVariable Long postId, Comment comment, HttpServletRequest request) throws ResponseStatusException {
//        Optional<Post> optionalPost = postService.getById(postId);
//        if(optionalPost.isPresent()) {
//            Post post = optionalPost.get();
//            comment.setPost(post);
//            setupComment(comment, request);
//            commentService.add(comment);
//            if(post.getThread() == null) {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No thread for the post");
//            }
//            return new RedirectView("/" + post.getThread().getUrl() + "/posts/{postId}");
//        }
//        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
//    }

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

    //@PostMapping("/posts/{postId}/comments/{commentId}/delete")
//    public RedirectView deleteComment(@PathVariable Long postId, @PathVariable Long commentId, String password, RedirectAttributes redirectAttr) throws ResponseStatusException{
//        Optional<Post> optionalPost = postService.getById(postId);
//        Optional<Comment> optionalComment = commentService.getById(commentId);
//        //Post is present, comment is present, thread is not null
//        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalPost.get().getThread() != null) {
//            Comment comment = optionalComment.get();
//            //Owner of the comment Anonymous user or registered user?
//            if(comment.getUser() == null) {
//                //Comment password is correct
//                if(passwordEncoder.matches(password, comment.getPassword())) {
//                    emptyComment(commentService,comment);
//                    redirectAttr.addFlashAttribute("successMessage","The Comment is deleted");
//                }
//                else {
//                    redirectAttr.addFlashAttribute("failMessage","Password is incorrect");
//                }
//            }
//            else {
//                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//                User authUser = (User) auth.getPrincipal();
//                //Current logged in user is the owner of the comment
//                if(comment.getUser().equals(authUser)) {
//                    emptyComment(commentService,comment);
//                    redirectAttr.addFlashAttribute("successMessage","The Comment is deleted");
//                }
//                else {
//                    redirectAttr.addFlashAttribute("failMessage","Access denied");
//                }
//            }
//            return new RedirectView("/" + optionalPost.get().getThread().getUrl() + "/posts/{postId}");
//        }
//        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
//    }

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

//    @GetMapping("/posts/{postId}/comments/{commentId}/reply")
//    public String newReply(@PathVariable Long postId, @PathVariable Long commentId, Model model) throws ResponseStatusException{
//        Optional<Post> optionalPost = postService.getById(postId);
//        Optional<Comment> optionalComment = commentService.getById(commentId);
//        //post and comment exist and the comment is from the post
//        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalComment.get().getPost().equals(optionalPost.get())) {
//            model.addAttribute("post",optionalPost.get());
//            model.addAttribute("parentComment",optionalComment.get());
//            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
//                model.addAttribute("currentUser", auth.getPrincipal());
//            }
//            return "reply";
//        }
//        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Bad request url");
//    }

    @PostMapping("/posts/{postId}/comments/{commentId}/reply")
    public RedirectView saveNewReply(@PathVariable Long postId, @PathVariable Long commentId, Comment reply, HttpServletRequest request) {
        Optional<Post> optionalPost = postService.getById(postId);
        Optional<Comment> optionalComment = commentService.getById(commentId);
        //post and comment exist and the comment is from the post
        if(optionalPost.isPresent() && optionalComment.isPresent() && optionalComment.get().getPost().equals(optionalPost.get())) {
            setupCommentUser(reply, request);
            reply.setPost(optionalPost.get());
            reply.setParentComment(optionalComment.get());
            commentService.add(reply);
            return new RedirectView("/posts/{postId}");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Bad request url");
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
