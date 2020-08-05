package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Optional;

@Controller
public class ThreadController {

    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;

    @GetMapping("/new_thread")
    public String createThread() {
        return "newThread";
    }

    @PostMapping("/new_thread")
    public RedirectView saveNewThread(String url, String name, String description) {
        Optional<Thread> optionalThread = threadService.getByUrl(url);
        if(optionalThread.isPresent()) {
            RedirectView urlAlreadyTaken = new RedirectView("/new_thread");

            return urlAlreadyTaken;
        }
        Thread thread = new Thread(url,name);
        threadRepository.save(thread);
        return new RedirectView("/");
    }
}
