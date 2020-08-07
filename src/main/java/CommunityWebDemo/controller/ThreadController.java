package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.ThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
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
    public String threadSetting(@PathVariable String threadUrl, Model model) {
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(!optionalThread.isPresent()) {
            return "error";
        }
        model.addAttribute("thread",optionalThread.get());
        return "threadSettings";
    }
}
