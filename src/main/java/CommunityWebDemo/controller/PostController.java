package CommunityWebDemo.controller;

import CommunityWebDemo.entity.*;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.security.IpHandler;
import CommunityWebDemo.service.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PostController implements OptionalEntityExceptionHandler {

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
    @Autowired
    VoteController voteController;
    @Autowired
    BookmarkController bookmarkController;
    @Autowired
    TagService tagService;

    IpHandler ipHandler = new IpHandler();



    @GetMapping("/{threadUrl}/posts/{id}")
    public String showPostById(@PathVariable String threadUrl, @PathVariable Long id, Model model, HttpServletRequest request) throws ResponseStatusException, JSONException {
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        Post post = getPostOrException(postService.getById(id));
        List<Comment> allComments = commentService.getCommentsOfPost(post);
        List<Comment> comments = new ArrayList<>();
        for(Comment comment : allComments) {
            if(comment.getParentComment() == null) {
                comments.add(comment);
            }
            if(comment.isActive()) {
                post.setNumberOfComments(post.getNumberOfComments() + 1);
            }
        }
        Parser parser = Parser.builder().build();
        HtmlRenderer htmlRenderer = HtmlRenderer.builder().escapeHtml(true).softbreak("<br>").build();
        Node node = parser.parse(post.getBody());
        post.setBody(htmlRenderer.render(node));
        model.addAttribute("thread",thread);
        model.addAttribute("post",post);
        model.addAttribute("comments",comments);
        //Check if current user is registered or anonymous
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            User currentUser = (User) auth.getPrincipal();
            model.addAttribute("currentUser",currentUser);
            model.addAttribute("bookmarked",bookmarkController.bookmark(threadUrl,id,"check"));
        }
        else {
            model.addAttribute("currentUser",null);
        }

        if(voteController.checkVoteBefore(threadUrl,id,true,request)) {
            model.addAttribute("upVoted",true);
            model.addAttribute("downVoted",false);
        }
        else if(voteController.checkVoteBefore(threadUrl,id,false,request)) {
            model.addAttribute("upVoted",false);
            model.addAttribute("downVoted",true);
        }
        else {
            model.addAttribute("upVoted",false);
            model.addAttribute("downVoted",false);
        }
        return "post";
    }

    @GetMapping("/{threadUrl}/new_post")
    public String newPost(@PathVariable String threadUrl, Model model) throws ResponseStatusException {
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        List<Tag> tags = tagService.getByThread(thread);
        model.addAttribute("thread",thread);
        model.addAttribute("tags",tags);
        return "newPost";
    }

    @PostMapping("/{threadUrl}/new_post")
    public RedirectView saveNewPost(@PathVariable String threadUrl, Post newPost, String stringTagId, HttpServletRequest request) throws ResponseStatusException {
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        if(stringTagId != null) {
            checkAndSetTagToPost(thread,newPost,stringTagId);
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Post author Anonymous or Registered
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            newPost.setIp(ipHandler.trimIpAddress(request.getRemoteAddr()));
            newPost.setPassword(passwordEncoder.encode(newPost.getPassword()));
        }
        else {
            User authUser = (User) auth.getPrincipal();
            newPost.setUser(authUser);
        }
        postService.add(newPost);
        return new RedirectView("/{threadUrl}/posts/"+newPost.getId());
    }

    @PostMapping("/{threadUrl}/posts/{id}/delete")
    public RedirectView delete(@PathVariable String threadUrl, @PathVariable Long id, String password, RedirectAttributes redirectAttr) throws ResponseStatusException{
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        Post post = getPostOrException(postService.getById(id));
        //the thread of the post is same
        if(post.getThread().equals(thread)) {
            List<Comment> comments = commentService.getCommentsOfPost(post);
            //Owner of the post Anonymous or Registered?
            if(post.getUser() == null) {
                //password match?
                if(passwordEncoder.matches(password, post.getPassword())) {
                    commentService.deleteAll(comments);
                    post.setIp(null);
                    post.setPassword(null);
                    post.setThread(null);
                    post.setActive(false);
                    postService.add(post);
                    redirectAttr.addFlashAttribute("successMessage","The post is deleted");
                    return new RedirectView("/{threadUrl}/");
                }
                //wrong password
                else {
                    redirectAttr.addFlashAttribute("failMessage","Wrong password");
                    return new RedirectView("/{threadUrl}/posts/{id}");
                }
            }
            //Registered user
            else {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                //logged in?
                if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                    User authUser = (User) auth.getPrincipal();
                    //Is current user the owner of the post?
                    if(post.getUser().equals(authUser)) {
                        commentService.deleteAll(comments);
                        post.setUser(null);
                        post.setThread(null);
                        post.setActive(false);
                        postService.add(post);
                        redirectAttr.addFlashAttribute("successMessage","The post is deleted");
                        return new RedirectView("/{threadUrl}/");
                    }
                    else {
                        redirectAttr.addFlashAttribute("failMessage","Access Denied");
                        return new RedirectView("/{threadUrl}/posts/{id}");
                    }
                }
                else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    @GetMapping("/{threadUrl}/posts/{id}/edit")
    public String updatePost(@PathVariable String threadUrl, @PathVariable Long id, Model model, RedirectAttributes redirectAttr) throws ResponseStatusException{
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        Post post = getPostOrException(postService.getById(id));
        //The thread is present, the post is present
        List<Tag> tags = tagService.getByThread(thread);
        model.addAttribute("tags",tags);
        model.addAttribute("thread",thread);
        model.addAttribute("post", post);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //is this post owned by registered user?
        if(post.getUser() != null) {
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //current user is the owner of the post
                if(post.getUser().equals(authUser)) {
                    return "updatePost";
                }
                //current user is not the owner of the post
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access denied");
                    return "redirect:/{threadUrl}/posts/{id}";
                }
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        //This post was written by anonymous user
        else {
            return "updatePost";
        }
    }

    @PostMapping("/{threadUrl}/posts/{id}/edit")
    public RedirectView saveUpdatedPost(@PathVariable String threadUrl,@PathVariable Long id, Post post, String stringTagId, RedirectAttributes redirectAttr) throws ResponseStatusException {
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        Post targetPost = getPostOrException(postService.getById(id));
        //apply changes
        targetPost.setTitle(post.getTitle());
        targetPost.setBody(post.getBody());
        if(stringTagId != null) {
            checkAndSetTagToPost(thread, targetPost, stringTagId);
        }

        //the owner of the post registered or anonymous?
        if(targetPost.getUser() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //logged in?
            if(!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                User authUser = (User) auth.getPrincipal();
                //current user is the owner of the post
                if(targetPost.getUser().equals(authUser)) {
                    postService.add(targetPost);
                }
                //wrong user
                else {
                    redirectAttr.addFlashAttribute("failMessage","Access denied");
                }
            }
            else throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        //owner of the post is anonymous
        else {
            //check password
            if(passwordEncoder.matches(post.getPassword(),targetPost.getPassword())) {
                postService.add(targetPost);
            }
            //wrong password
            else {
                redirectAttr.addFlashAttribute("failMessage","Wrong password");
                //redirectAttr.addFlashAttribute("post",post);
                return new RedirectView("/{threadUrl}/posts/{id}/edit");
            }
        }
        return new RedirectView("/{threadUrl}/posts/{id}");
    }

    @GetMapping("/posts/{id}")
    public RedirectView redirectPostToThread(@PathVariable Long id) throws ResponseStatusException{
        Post post = getPostOrException(postService.getById(id));
        Thread thread = getThreadOrException(threadService.getByUrl(post.getThread().getUrl()));
        return new RedirectView("/"+thread.getUrl()+"/posts/{id}");
    }

    private void checkAndSetTagToPost(Thread thread, Post post, String stringTagId) {
        Long tagId = Long.valueOf(stringTagId);
        List<Tag> tags = tagService.getByThread(thread);
        post.setThread(thread);
        for(Tag tag : tags) {
            if(tag.getId().equals(tagId)) {
                post.setTag(tag);
                break;
            }
        }
    }

}
