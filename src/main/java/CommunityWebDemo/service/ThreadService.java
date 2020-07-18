package CommunityWebDemo.service;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThreadService {

    @Autowired
    ThreadRepository threadRepository;

    public Optional<Thread> getByInitial(String initial) {
        return threadRepository.findById(initial);
    }
}
