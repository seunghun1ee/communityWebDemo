package CommunityWebDemo;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.PostRepository;
import CommunityWebDemo.repository.UserRepository;
import CommunityWebDemo.service.PostService;
import CommunityWebDemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class ServiceTests {

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    void addPostTest() {
        postRepository.deleteAll();
        Post post0 = new Post("title", "body");
        Post post1 = new Post("title2","body");
        List<Post> result = new ArrayList<>();
        postService.add(post0);
        postService.add(post1);
        postRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void updatePostTest() {
        postRepository.deleteAll();
        List<Post> result = new ArrayList<>();
        postService.add(new Post("update this title","body"));
        postService.add(new Post("don't edit this title","body"));
        postRepository.findAll().forEach(result::add);
        Post postEdit = new Post();
        for(Post post : result) {
            if(post.getTitle().equals("update this title")) {
                postEdit = post;
                break;
            }
        }
        assertThat(postEdit.getTitle()).isEqualTo("update this title");
        Long editId = postEdit.getId();
        postEdit.setTitle("new title");
        postService.add(postEdit);
        assertThat(postRepository.findById(editId).isPresent()).isTrue();
        assertThat(postRepository.findById(editId).get().getTitle()).isEqualTo("new title");
    }

    @Test
    void getAllPostTest() {
        postRepository.deleteAll();
        postRepository.save(new Post("post1","body"));
        postRepository.save(new Post("post2","body"));
        postRepository.save(new Post("post3","body"));
        assertThat(postService.getAll().size()).isEqualTo(3);
    }

    @Test
    void getPostByIdTest() {
        postRepository.deleteAll();
        List<Post> posts = new ArrayList<>();
        Post targetPost = new Post("get this post","body");
        postRepository.save(targetPost);
        postRepository.save(new Post("post2","body"));
        postRepository.save(new Post("post3","body"));
        postRepository.findAll().forEach(posts::add);
        Post getThisPost = new Post();
        for(Post post : posts) {
            if(post.getTitle().equals("get this post")) {
                getThisPost = post;
                break;
            }
        }
        targetPost.setId(getThisPost.getId());
        assertThat(postService.getById(getThisPost.getId()).isPresent()).isTrue();
        assertThat(postService.getById(getThisPost.getId()).get()).isEqualTo(targetPost);
    }

    @Test
    void deletePostTest() {
        postRepository.deleteAll();
        Post targetPost = new Post("Delete this","body");
        Post post = new Post("Don't delete this","body");
        postRepository.save(targetPost);
        postRepository.save(post);
        List<Post> posts = postService.getAll();
        for(Post post1 : posts) {
            if(post1.getTitle().equals("Delete this")) {
                targetPost = post1;
                break;
            }
        }
        postService.deleteById(targetPost.getId());
        List<Post> result = postService.getAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Don't delete this");
    }

    @Test
    void deleteAllPostTest() {
        postRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            postRepository.save(new Post());
        }
        postService.deleteAll();
        assertThat(postService.getAll().size()).isEqualTo(0);
    }

    @Test
    void getAllUsersTest() {
        userRepository.deleteAll();
        for(int i=0; i < 3; i++) {
            userRepository.save(new User());
        }
        assertThat(userService.getAll().size()).isEqualTo(3);
    }

    @Test
    void addUserTest() {
        userRepository.deleteAll();
        for(int i=0; i < 2; i++) {
            userService.add(new User());
        }
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void updateUserTest() {
        userRepository.deleteAll();
        userRepository.save(new User("change me"));
        userRepository.save(new User("don't"));
        List<User> users = new ArrayList<>();
        User target = new User();
        userRepository.findAll().forEach(users::add);
        for(User user : users) {
            if(user.getName().equals("change me")) {
                target = user;
                break;
            }
        }
        Long targetId = target.getId();
        target.setName("new name");
        userService.add(target);
        assertThat(userRepository.findById(targetId).isPresent()).isTrue();
        assertThat(userRepository.findById(targetId).get().getName()).isEqualTo("new name");
    }

    @Test
    void getUserByIdTest() {
        userRepository.deleteAll();
        User target = new User("target");
        userRepository.save(target);
        userRepository.save(new User("user2"));
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        for(User user : users) {
            if(user.getName().equals("target")) {
                target = user;
            }
        }
        assertThat(userService.getById(target.getId()).isPresent()).isTrue();
        assertThat(userService.getById(target.getId())).isEqualTo(target);
    }

    @Test
    void deleteUserByIdTest() {
        userRepository.deleteAll();
        User target = new User("delete");
        userRepository.save(target);
        userRepository.save(new User("don't"));
        List<User> users = userService.getAll();
        for(User user : users) {
            if(user.getName().equals("delete")) {
                target = user;
                break;
            }
        }
        userService.deleteById(target.getId());
        List<User> result = userService.getAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("delete");
    }

    @Test
    void deleteAllUserTest() {
        userRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            userRepository.save(new User());
        }
        userService.deleteAll();
        assertThat(userService.getAll().size()).isEqualTo(0);
    }
}
