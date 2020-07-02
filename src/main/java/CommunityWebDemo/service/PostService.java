package CommunityWebDemo.service;

import CommunityWebDemo.FakeDB;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    @Autowired
    PostRepository postRepository;

    public List<Post> getAllPosts(FakeDB db) {
        return db.postTable;
    }

    public void addPost(FakeDB db, Post post) {
        db.postTable.add(post);
    }
}
