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

    @Test
    void getPostByIdTest() {
        postRepository.deleteAll();
        List<Post> posts = new ArrayList<>();
        Post targetPost = new Post("get this post","body",0L);
        postRepository.save(targetPost);
        postRepository.save(new Post("post2","body",1L));
        postRepository.save(new Post("post3","body",3L));
        postRepository.findAll().forEach(posts::add);
        Post getThisPost = new Post();
        for(Post post : posts) {
            if(post.getTitle().equals("get this post")) {
                getThisPost = post;
                break;
            }
        }
        targetPost.setId(getThisPost.getId());
        assert(postService.getPostById(getThisPost.getId()).isPresent());
        assert(postService.getPostById(getThisPost.getId()).get().equals(targetPost));
    }

    @Test
    void deletePostTest() {
        postRepository.deleteAll();
        Post targetPost = new Post("Delete this","body",0L);
        Post post = new Post("Don't delete this","body",0L);
        postRepository.save(targetPost);
        postRepository.save(post);
        List<Post> posts = postService.getAllPosts();
        for(Post post1 : posts) {
            if(post1.getTitle().equals("Delete this")) {
                targetPost = post1;
                break;
            }
        }
        postService.deletePostById(targetPost.getId());
        List<Post> result = postService.getAllPosts();
        assert(result.size() == 1);
        assert(result.get(0).getTitle().equals("Don't delete this"));
    }

    @Test
    void deleteAllTest() {
        postRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            postRepository.save(new Post());
        }
        postService.deleteAll();
        assert(postService.getAllPosts().size() == 0);
    }
}
