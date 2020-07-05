package CommunityWebDemo;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.entity.User;
import CommunityWebDemo.repository.PostRepository;
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

    @Test
    void postAndUserRelationshipTest() {
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
}
