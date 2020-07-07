package CommunityWebDemo;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.CommentRepository;
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
    @Autowired
    CommentRepository commentRepository;

    @Test
    void idGeneratedValueTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        Post post = new Post();
        postService.add(post);
        List<Post> posts = postService.getAll();
        assertThat(posts.get(0)).isEqualTo(post);
    }

    @Test
    void addPostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        Post post0 = new Post("title", "body");
        Post post1 = new Post("title2","body");
        List<Post> result = new ArrayList<>();
        postService.add(post0);
        postService.add(post1);
        postRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void addAllPostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        List<Post> posts = new ArrayList<>();
        for(int i=0; i < 8; i++) {
            posts.add(new Post());
        }
        postService.addAll(posts);
        List<Post> result = new ArrayList<>();
        postRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(8);
    }

    @Test
    void updatePostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        Post postEdit = new Post("update this title","body");
        Post post = new Post("don't edit this title","body");
        postService.add(postEdit);
        postService.add(post);
        postEdit.setTitle("new title");
        postService.add(postEdit);
        assertThat(postRepository.findById(postEdit.getId()).isPresent()).isTrue();
        assertThat(postRepository.findById(postEdit.getId()).get().getTitle()).isEqualTo("new title");
    }

    @Test
    void getAllPostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        postRepository.save(new Post("post1","body"));
        postRepository.save(new Post("post2","body"));
        postRepository.save(new Post("post3","body"));
        assertThat(postService.getAll().size()).isEqualTo(3);
    }

    @Test
    void getPostByIdTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        Post targetPost = new Post("get this post","body");
        postRepository.save(targetPost);
        postRepository.save(new Post("post2","body"));
        postRepository.save(new Post("post3","body"));
        assertThat(postService.getById(targetPost.getId()).isPresent()).isTrue();
        assertThat(postService.getById(targetPost.getId()).get()).isEqualTo(targetPost);
    }

    @Test
    void deletePostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        Post targetPost = new Post("Delete this","body");
        Post post = new Post("Don't delete this","body");
        postRepository.save(targetPost);
        postRepository.save(post);
        postService.deleteById(targetPost.getId());
        List<Post> result = postService.getAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Don't delete this");
    }

    @Test
    void deleteAllPostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            postRepository.save(new Post());
        }
        postService.deleteAll();
        assertThat(postService.getAll().size()).isEqualTo(0);
    }

    @Test
    void deleteListOfPostsTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            postRepository.save(new Post());
        }
        List<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        posts.remove(2);
        posts.remove(1);
        postService.deleteAll(posts);
        List<Post> result = new ArrayList<>();
        postRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getAllUsersTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 3; i++) {
            userRepository.save(new User());
        }
        assertThat(userService.getAll().size()).isEqualTo(3);
    }

    @Test
    void addUserTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 2; i++) {
            userService.add(new User());
        }
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void addUserListTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        List<User> users = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            users.add(new User());
        }
        userService.addAll(users);
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    void updateUserTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        User target = new User("change me");
        User user = new User("don't");
        userRepository.save(target);
        userRepository.save(user);
        target.setName("new name");
        userService.add(target);
        assertThat(userRepository.findById(target.getId()).isPresent()).isTrue();
        assertThat(userRepository.findById(target.getId()).get().getName()).isEqualTo("new name");
    }

    @Test
    void getUserByIdTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        User target = new User("target");
        userRepository.save(target);
        userRepository.save(new User("user2"));
        assertThat(userService.getById(target.getId()).isPresent()).isTrue();
        assertThat(userService.getById(target.getId()).get()).isEqualTo(target);
    }

    @Test
    void deleteUserByIdTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        User target = new User("delete");
        userRepository.save(target);
        userRepository.save(new User("don't"));
        userService.deleteById(target.getId());
        List<User> result = userService.getAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getName()).isEqualTo("don't");
    }

    @Test
    void deleteAllUserTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            userRepository.save(new User());
        }
        userService.deleteAll();
        assertThat(userService.getAll().size()).isEqualTo(0);
    }

    @Test
    void deleteUserListTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        for(int i=0; i < 5; i++) {
            userRepository.save(new User());
        }
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        users.remove(2);
        users.remove(1);
        userService.deleteAll(users);
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findPostOfUserTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(new User("tester"));
        User user = userService.getAll().get(0);
        postRepository.save(new Post("find1","body",user));
        postRepository.save(new Post("find2","body",user));
        postRepository.save(new Post("find3","body",user));
        for(int i=0; i < 3; i++) {
            postRepository.save(new Post());
        }
        List<Post> posts = postService.findPostsOfUser(user);
        assertThat(posts.size()).isNotZero();
        for(Post post : posts) {
            assertThat(post.getUser()).isEqualTo(user);
        }
    }
}
