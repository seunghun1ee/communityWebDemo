package CommunityWebDemo.service;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ThreadService {

    @Autowired
    ThreadRepository threadRepository;

    public Optional<Thread> getByUrl(String url) {
        return threadRepository.findById(url);
    }

    public List<Thread> getByName(String name) {
        List<Thread> threads = new ArrayList<>();
        threadRepository.findAll().forEach(thread -> {
            if(thread.getName().equals(name)) {
                threads.add(thread);
            }
        });
        return threads;
    }
}
