package CommunityWebDemo.controller;

import CommunityWebDemo.compartor.SortByPostDateTime;
import CommunityWebDemo.compartor.SortByPostVote;
import CommunityWebDemo.entity.*;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.TagService;
import CommunityWebDemo.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ThreadController implements OptionalEntityExceptionHandler {

    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentController commentController;
    @Autowired
    TagService tagService;


    @GetMapping(value = {"/{threadUrl}/","/{threadUrl}"})
    public String showAllPostsOfThread(@PathVariable String threadUrl, @RequestParam(required = false, defaultValue = "date") String sort, Model model) throws ResponseStatusException {
        Thread thread = getThreadOrException(threadService.getByUrl(threadUrl));
        List<Post> posts = postService.getPostsOfThread(thread);
        for(Post post : posts) {
            commentController.setActiveCommentNumber(post);
        }
        switch (sort) {
            case "vote":
                posts.sort(new SortByPostVote());
                break;
            case "date":
            default:
                posts.sort(new SortByPostDateTime());
                break;
        }
        model.addAttribute("thread",thread);
        model.addAttribute("posts",posts);
        List<Tag> tags = tagService.getByThread(thread);
        model.addAttribute("tags",tags);
        return "thread";
    }

    @GetMapping("/new_thread")
    public String createThread() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return "redirect:/login";
        }
        return "newThread";
    }

    @PostMapping("/new_thread")
    public String saveNewThread(Model model, String url, String name, String description, RedirectAttributes redirectAttr) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Access denied");
        }
        User authUser = (User) auth.getPrincipal();
        Optional<Thread> urlCheck = threadService.getByUrl(url);
        if(urlCheck.isPresent()) {
            model.addAttribute("failMessage","This url is already in use");
            return "newThread";
        }
        List<Thread> nameCheck = threadService.getByName(name);
        if(!nameCheck.isEmpty()) {
            model.addAttribute("failMessage","This name is already in use");
            return "newThread";
        }
        Thread thread = new Thread(url, name, description, authUser);
        threadRepository.save(thread);
        redirectAttr.addFlashAttribute("successMessage","The thread is opened");
        return "redirect:/"+url+"/";
    }

    //@GetMapping("/{threadUrl}/settings")
    public String threadSetting(@PathVariable String threadUrl, Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            model.addAttribute("thread",optionalThread.get());
            return "threadSettings";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    //@PostMapping("/{threadUrl}/settings/edit")
    public String saveThreadSetting(@PathVariable String threadUrl, Model model, String description) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            Thread thread = optionalThread.get();
            if(description == null) {
                model.addAttribute("failMessage","Saving failed");
            }
            else {
                thread.setDescription(description);
                model.addAttribute("successMessage","Saved change");
            }
            model.addAttribute("thread",thread);
            threadRepository.save(thread);
            return "threadSettings";
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }

    //@PostMapping("/{threadUrl}/delete")
    public RedirectView deleteThread(@PathVariable String threadUrl) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            Thread thread = optionalThread.get();
            List<Post> posts = new ArrayList<>();
            List<Post> allPosts = postService.getAll();
            allPosts.forEach(post -> {
                if(post.getThread().equals(thread)) {
                    posts.add(post);
                }
            });
            List<Comment> comments = new ArrayList<>();
            List<Comment> allComments = commentService.getAll();
            for(Post post : posts) {
                allComments.forEach(comment -> {
                    if(comment.getPost().equals(post)) {
                        comments.add(comment);
                    }
                });
            }
            commentService.deleteAll(comments);
            postService.deleteAll(posts);
            threadRepository.delete(thread);
            return new RedirectView("/");
        }
        else throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid request url");
    }
}
