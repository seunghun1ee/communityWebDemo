package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Tag;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.TagRepository;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Controller
public class TagController implements OptionalEntityExceptionHandler{

    @Autowired
    ThreadService threadService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;

    @GetMapping("/{threadId}/tags/new_tag")
    public String showNewTagPage(@PathVariable String threadId, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return "redirect:/login";
        }
        Thread thread = getThreadOrException(threadService.getByUrl(threadId));
        model.addAttribute("thread",thread);
        return "newTag";
    }

    @PostMapping("/{threadId}/tags/new_tag")
    public RedirectView saveNewTag(@PathVariable String threadId, String tagName, String colour, RedirectAttributes redirectAttr) {
        Thread thread = getThreadOrException(threadService.getByUrl(threadId));
        Tag newTag = new Tag(tagName,colour,thread);
        List<Tag> tags = tagService.getByThread(thread);
        for(Tag tag : tags) {
            if(tag.getTagName().equals(tagName)) {
                redirectAttr.addFlashAttribute("failMessage","The tag already exists");
                return new RedirectView("/{threadId}/tags/new_tag");
            }
        }
        tagRepository.save(newTag);
        return new RedirectView("/{threadId}/");
    }
}
