package CommunityWebDemo.service;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PostService extends MyService<Post>{

    @Autowired
    private PostRepository postRepository;

    @Override
    public void add(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        return posts;
    }

    @Override
    public Optional<Post> getById(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        if(postRepository.findById(id).isPresent()) {
            postRepository.deleteById(id);
            return true;
        }
        else return false;
    }

    @Override
    public void deleteAll() {
        postRepository.deleteAll();
    }

}
