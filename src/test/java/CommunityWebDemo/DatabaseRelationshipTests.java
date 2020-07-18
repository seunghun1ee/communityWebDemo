package CommunityWebDemo;

import CommunityWebDemo.entity.Comment;
import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.CommentRepository;
import CommunityWebDemo.repository.PostRepository;
import CommunityWebDemo.repository.ThreadRepository;
import CommunityWebDemo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DatabaseRelationshipTests {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ThreadRepository threadRepository;

    @Test
    void postAndUserRelationshipTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("dave"));
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        User dave = users.get(0);
        postRepository.save(new Post("dave's post","asdf",dave));
        List<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        Post davePost = posts.get(0);
        assertThat(davePost.getUser()).isEqualTo(dave);
    }

    @Test
    void multiplePostUserRelationshipTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("dave"));
        userRepository.save(new User("gary"));
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        User dave = new User();
        User gary = new User();
        for(User user : users) {
            if(user.getName().equals("dave")) {
                dave = user;
            }
            else {
                gary = user;
            }
        }
        postRepository.save(new Post("dave's first post","first",dave));
        postRepository.save(new Post("dave's second post","second",dave));
        postRepository.save(new Post("gary's only post","gary",gary));

        List<Post> posts = new ArrayList<>();
        List<Post> daves = new ArrayList<>();
        List<Post> garys = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        for(Post post : posts) {
            if(post.getUser().equals(dave)) {
                daves.add(post);
            }
            else {
                garys.add(post);
            }
        }
        assertThat(daves.size()).isEqualTo(2);
        assertThat(garys.size()).isEqualTo(1);
    }

    @Test
    void postUserCommentRelationshipTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("dave"));
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        User dave = users.get(0);
        postRepository.save(new Post("dave's post","asdf",dave));
        List<Post> posts = new ArrayList<>();
        postRepository.findAll().forEach(posts::add);
        Post davePost = posts.get(0);
        commentRepository.save(new Comment(davePost,dave,"test"));
        List<Comment> comments = new ArrayList<>();
        commentRepository.findAll().forEach(comments::add);
        Comment daveComment = comments.get(0);
        assertThat(daveComment.getPost()).isEqualTo(davePost);
        assertThat(daveComment.getUser()).isEqualTo(dave);
    }

    @Test
    void threadCreationTest() {
        threadRepository.deleteAll();
        Thread threadA = new Thread("a","Thread A");
        Thread threadB = new Thread("b","Thread B");
        Thread threadC = new Thread("c","Thread C");
        threadRepository.save(threadA);
        threadRepository.save(threadB);
        threadRepository.save(threadC);
        assertThat(threadA.getInitial()).isEqualTo("a");
        assertThat(threadB.getInitial()).isEqualTo("b");
        assertThat(threadC.getInitial()).isEqualTo("c");

    }

    @Test
    void threadPostRelationshipTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        threadRepository.deleteAll();
        Thread threadA = new Thread("a","thread a");
        Thread threadB = new Thread("b","thread b");
        threadRepository.save(threadA);
        threadRepository.save(threadB);
        List<Post> threadAPosts = new ArrayList<>();
        List<Post> threadBPosts = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            threadAPosts.add(new Post(threadA));
            threadBPosts.add(new Post(threadB));
        }
        postRepository.saveAll(threadAPosts);
        postRepository.saveAll(threadBPosts);

        List<Post> result = (List<Post>) postRepository.findAll();
        int a = 0;
        int b = 0;

        for(Post post : result) {
            switch (post.getThread().getInitial()) {
                case "a":
                    a++;
                    break;
                case "b":
                    b++;
                    break;
            }
        }
        assertThat(a).isEqualTo(3);
        assertThat(b).isEqualTo(3);
    }
}
