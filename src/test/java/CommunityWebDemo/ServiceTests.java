package CommunityWebDemo;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.repository.PostRepository;
import CommunityWebDemo.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ServiceTests {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;

    @Test
    void addPostTest() {
        postRepository.deleteAll();
        Post post0 = new Post(1L, "title", "body", 0L);
        Post post1 = new Post("title2","body",1L);
        List<Post> result = new ArrayList<>();
        postService.addPost(post0);
        postService.addPost(post1);
        postRepository.findAll().forEach(result::add);
        assert(result.size() == 2);
    }

    @Test
    void updatePostTest() {
        postRepository.deleteAll();
        List<Post> result = new ArrayList<>();
        postService.addPost(new Post("update this title","body",4L));
        postService.addPost(new Post("don't edit this title","body",4L));
        postRepository.findAll().forEach(result::add);
        Post postEdit = new Post();
        for(Post post : result) {
            if(post.getTitle().equals("update this title")) {
                postEdit = post;
                break;
            }
        }
        assert(postEdit.getTitle().equals("update this title"));
        Long editId = postEdit.getId();
        postEdit.setTitle("new title");
        postService.addPost(postEdit);
        assert(postRepository.findById(editId).isPresent());
        assert(postRepository.findById(editId).get().getTitle().equals("new title"));
    }

    @Test
    void getAllPostTest() {
        postRepository.deleteAll();
        postRepository.save(new Post("post1","body",0L));
        postRepository.save(new Post("post2","body",1L));
        postRepository.save(new Post("post3","body",3L));
        assert(postService.getAllPosts().size() == 3);
    }

}
