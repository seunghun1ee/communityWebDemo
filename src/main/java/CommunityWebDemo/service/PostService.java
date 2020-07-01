package CommunityWebDemo.service;

import CommunityWebDemo.entity.Post;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    public List<Post> getAllPosts() {
        return new ArrayList<>();
    }

    public void addPost(Post post) {

    }
}
