package CommunityWebDemo.controller;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.service.ThreadService;
import CommunityWebDemo.service.UserService;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
public class SubscribeController {

    @Autowired
    ThreadService threadService;
    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    UserService userService;

    @PostMapping("/{threadUrl}/checkSubscribers")
    public boolean checkSubscriber(@PathVariable String threadUrl) throws JSONException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Anonymous user, no need to check
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            return false;
        }
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            Thread thread = optionalThread.get();
            User authUser = (User) auth.getPrincipal();
            JSONArray subscribers = new JSONArray(thread.getSubscribers());
            return isSubscribed(subscribers,authUser);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Thread not found");
    }

    @PostMapping("/{threadUrl}/subscribe")
    public boolean subscribe(@PathVariable String threadUrl) throws JSONException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //Anonymous user, can't subscribe
        if(auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"illegal subscription request");
        }
        Optional<Thread> optionalThread = threadService.getByUrl(threadUrl);
        if(optionalThread.isPresent()) {
            Thread thread = optionalThread.get();
            User authUser = (User) auth.getPrincipal();
            JSONArray subscribers = new JSONArray(thread.getSubscribers());
            JSONArray subscribedThreads = new JSONArray(authUser.getSubscribedThreads());
            if(isSubscribed(subscribers,authUser)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"already subscribed");
            }
            else {
                subscribers.put(authUser.getId());
                subscribedThreads.put(threadUrl);
                thread.setSubscribers(subscribers.toString());
                authUser.setSubscribedThreads(subscribedThreads.toString());
                threadRepository.save(thread);
                userService.add(authUser);
                return true;
            }
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Thread not found");
    }

    private boolean isSubscribed(JSONArray subscribers, User authUser) throws JSONException {

        for(int i = 0; i < subscribers.length(); i++) {
            if(subscribers.optLong(i) == authUser.getId()) {
                return true;
            }
        }
        return false;
    }
}
