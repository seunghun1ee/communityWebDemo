package CommunityWebDemo.service;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public void addPost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        return posts;
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

}
