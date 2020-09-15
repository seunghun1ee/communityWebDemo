package CommunityWebDemo.service;

import CommunityWebDemo.entity.Tag;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {
    @Autowired
    TagRepository tagRepository;

    public List<Tag> getByThread(Thread thread) {
        List<Tag> tags = new ArrayList<>();
        tagRepository.findAll().forEach(tag -> {
            if(tag.getThread() != null && tag.getThread().equals(thread)) {
                tags.add(tag);
            }
        });
        return tags;
    }
}
