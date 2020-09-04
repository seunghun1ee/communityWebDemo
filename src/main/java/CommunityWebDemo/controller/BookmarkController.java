package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.ThreadRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class BookmarkController implements OptionalEntityExceptionHandler{

    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    @PostMapping("/{threadUrl}/posts/{postId}/bookmark")
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

    private boolean isBookmarked(Post post, JSONObject bookmarksJSON) {
        return !bookmarksJSON.isNull(post.getId().toString());
    }
}
