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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    public ModelAndView saveNewThread(String url, String name, String description) {
        ModelAndView modelAndView = new ModelAndView("home");
        Optional<Thread> urlCheck = threadService.getByUrl(url);
        if(urlCheck.isPresent()) {
            modelAndView.setViewName("newThread");
            modelAndView.addObject("urlTakenError","This url is already in use");
        }
        List<Thread> nameCheck = threadService.getByName(name);
        if(!nameCheck.isEmpty()) {
            modelAndView.setViewName("newThread");
            modelAndView.addObject("nameTakenError","This name is already in use");
        }
        Thread thread = new Thread(url,name);
        threadRepository.save(thread);
        return modelAndView;
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
