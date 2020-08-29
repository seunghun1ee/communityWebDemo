package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.CommentService;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class ThreadController {

    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    @GetMapping("/{threadUrl}/")
    public String showAllPostsOfThread(@PathVariable String threadUrl,Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            List<Post> posts = postService.getPostsOfThread(optionalThread.get());
            model.addAttribute("thread",optionalThread.get());
            model.addAttribute("posts",posts);
            return "postList";
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    @GetMapping("/new_thread")
    public String createThread() {
        return "newThread";
    }

    @PostMapping("/new_thread")
    public String saveNewThread(Model model, String url, String name, String description) {
        Optional<Thread> urlCheck = threadService.getByUrl(url);
        if(urlCheck.isPresent()) {
            model.addAttribute("urlTakenError","This url is already in use");
            return "newThread";
        }
        List<Thread> nameCheck = threadService.getByName(name);
        if(!nameCheck.isEmpty()) {
            model.addAttribute("nameTakenError","This name is already in use");
            return "newThread";
        }
        Thread thread = new Thread(url, name, description);
        threadRepository.save(thread);
        model.addAttribute("successMessage","The thread is opened");
        return "newThread";
    }

    @GetMapping("/{threadUrl}/settings")
    public String threadSetting(@PathVariable String threadUrl, Model model) throws ResponseStatusException {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            model.addAttribute("thread",optionalThread.get());
            return "threadSettings";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Page not found");
    }

    @PostMapping("/{threadUrl}/settings/edit")
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

    @PostMapping("/{threadUrl}/delete")
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
