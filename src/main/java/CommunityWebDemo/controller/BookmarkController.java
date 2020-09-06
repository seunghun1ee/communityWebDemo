package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.ThreadRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Controller
public class BookmarkController implements OptionalEntityExceptionHandler{

    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    @PostMapping("/{threadUrl}/posts/{postId}/bookmark")
    @ResponseBody
    public boolean bookmark(@PathVariable String threadUrl, @PathVariable Long postId, @RequestParam String mode) throws JSONException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Illegal request");
        }
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        Optional<Post> optionalPost = postService.getById(postId);
        getThreadOrException(optionalThread);
        Post post = getPostOrException(optionalPost);
        User authUser = (User) auth.getPrincipal();
        JSONObject bookmarksJSON = new JSONObject(authUser.getBookmarks());
        switch (mode) {
            case "mark":
                if(isBookmarked(post,bookmarksJSON)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"already bookmarked");
                }
                bookmarksJSON.put(post.getId().toString(),true);
                authUser.setBookmarks(bookmarksJSON.toString());
                userService.add(authUser);
                return true;
            case "unmark":
                if(!isBookmarked(post,bookmarksJSON)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"not bookmarked before");
                }
                bookmarksJSON.remove(post.getId().toString());
                authUser.setBookmarks(bookmarksJSON.toString());
                userService.add(authUser);
                return true;
            case "check":
            default:
                return isBookmarked(post,bookmarksJSON);
        }

    }

    @GetMapping("/users/{id}/bookmarks")
    public String showBookmarksOfUser(@PathVariable Long id, Model model) throws JSONException {
        Optional<User> optionalUser = userService.getById(id);
        User user = getUserOrException(optionalUser);
        Authentication auth =SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            User authUser = (User) auth.getPrincipal();
            if(user.equals(authUser)) {
                List<Post> bookmarkedPosts = new ArrayList<>();
                JSONObject bookmarkedPostsJSON = new JSONObject(user.getBookmarks());
                Iterator<String> bookmarkedPostIds = bookmarkedPostsJSON.keys();
                while (bookmarkedPostIds.hasNext()) {
                    String postId = bookmarkedPostIds.next();
                    Optional<Post> optionalPost = postService.getById(Long.valueOf(postId));
                    if(optionalPost.isPresent()) {
                        Post post = optionalPost.get();
                        List<Comment> comments = commentService.getCommentsOfPost(post);
                        List<Comment> activeComments = new ArrayList<>();
                        comments.forEach(comment -> {
                            if(comment.isActive()) {
                                activeComments.add(comment);
                            }
                        });
                        post.setNumberOfComments(activeComments.size());
                        bookmarkedPosts.add(post);
                    }
                }
                model.addAttribute("bookmarkedPosts",bookmarkedPosts);
                return "bookmarks";
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        else throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    private boolean isBookmarked(Post post, JSONObject bookmarksJSON) {
        return !bookmarksJSON.isNull(post.getId().toString());
    }
}
