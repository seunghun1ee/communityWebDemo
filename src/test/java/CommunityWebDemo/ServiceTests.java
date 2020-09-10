package CommunityWebDemo;

import CommunityWebDemo.entity.*;
import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.*;
import CommunityWebDemo.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    @Autowired
    CommentService commentService;
    @Autowired
    ThreadRepository threadRepository;
    @Autowired
    ThreadService threadService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    TagService tagService;

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
        target.setUsername("new name");
        userService.add(target);
        assertThat(userRepository.findById(target.getId()).isPresent()).isTrue();
        assertThat(userRepository.findById(target.getId()).get().getUsername()).isEqualTo("new name");
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
        assertThat(result.get(0).getUsername()).isEqualTo("don't");
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
        List<Post> posts = postService.getPostsOfUser(user);
        assertThat(posts.size()).isNotZero();
        for(Post post : posts) {
            assertThat(post.getUser()).isEqualTo(user);
        }
    }

    @Test
    void addCommentTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        commentService.add(new Comment());
        List<Comment> comments = (List<Comment>) commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(1);
    }

    @Test
    void updateCommentTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User("test");
        userRepository.save(testUser);
        Post testPost = new Post("test","body",testUser);
        postRepository.save(testPost);
        Comment testComment = new Comment(testPost,testUser,"change");
        commentService.add(testComment);
        testComment.setMessage("new");
        commentService.add(testComment);
        Optional<Comment> optionalComment = commentRepository.findById(testComment.getId());
        assertThat(optionalComment.isPresent()).isTrue();
        assertThat(optionalComment.get().getMessage()).isEqualTo("new");
    }

    @Test
    void addMultipleComments() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        for(int i=0; i < 4; i++) {
            commentService.add(new Comment());
        }
        List<Comment> comments = (List<Comment>) commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(4);
    }

    @Test
    void addCommentListTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        List<Comment> comments = new ArrayList<>();
        for(int i=0; i < 4; i++) {
            comments.add(new Comment());
        }
        commentService.addAll(comments);
        List<Comment> result = (List<Comment>) commentRepository.findAll();
        assertThat(result.size()).isEqualTo(4);
    }

    @Test
    void getAllCommentsTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        for(int i=0; i < 3; i++) {
            commentService.add(new Comment());
        }
        List<Comment> comments = commentService.getAll();
        assertThat(comments.size()).isEqualTo(3);
    }

    @Test
    void getCommentByIdTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        Comment target = new Comment();
        commentRepository.save(target);
        commentRepository.save(new Comment());
        assertThat(commentService.getById(target.getId()).isPresent()).isTrue();
        assertThat(commentService.getById(target.getId()).get()).isEqualTo(target);
    }

    @Test
    void deleteCommentByIdTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        Comment target = new Comment();
        commentRepository.save(target);
        commentRepository.save(new Comment());
        assertThat(commentService.deleteById(target.getId())).isTrue();
        assertThat(commentService.getById(target.getId()).isPresent()).isFalse();
    }

    @Test
    void deleteAllCommentsTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        for(int i=0; i < 5; i++) {
            commentRepository.save(new Comment());
        }
        commentService.deleteAll();
        List<Comment> comments = (List<Comment>) commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(0);
    }

    @Test
    void deleteSomeCommentsTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
        List<Comment> comments = new ArrayList<>();
        for(int i=0; i < 5; i++) {
            Comment comment = new Comment();
            commentRepository.save(comment);
            comments.add(comment);
        }
        comments.remove(1);
        comments.remove(1);
        commentService.deleteAll(comments);
        List<Comment> result = (List<Comment>) commentRepository.findAll();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void getCommentsOfPostTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User("test");
        userRepository.save(user);
        Post post = new Post("test","body",user);
        Post post1 = new Post("no","body",user);
        postRepository.save(post);
        postRepository.save(post1);
        for(int i=0; i < 3; i++) {
            commentRepository.save(new Comment(post,user,"0"));
            commentRepository.save(new Comment(post1,user,"1"));
        }
        List<Comment> postComments = commentService.getCommentsOfPost(post);
        assertThat(postComments.size()).isEqualTo(3);
    }

    @Test
    void getPostsOfThreadTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        threadRepository.deleteAll();

        Thread threadA = new Thread("a","thread a");
        Thread threadB = new Thread("b","thread b");
        threadRepository.save(threadA);
        threadRepository.save(threadB);

        for(int i = 0; i < 3; i++) {
            postRepository.save(new Post(threadA));
        }
        for(int i = 0; i < 2; i++) {
            postRepository.save(new Post(threadB));
        }
        List<Post> posts = postService.getPostsOfThread(threadA);
        assertThat(posts.size()).isEqualTo(3);

    }

    @Test
    void getThreadByUrlTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        threadRepository.deleteAll();

        Thread threadA = new Thread("a","thread a");
        Thread threadB = new Thread("b","thread b");
        threadRepository.save(threadA);
        threadRepository.save(threadB);

        assertThat(threadService.getByUrl(threadA.getUrl()).isPresent()).isTrue();
        assertThat(threadService.getByUrl(threadA.getUrl()).get()).isEqualTo(threadA);
        assertThat(threadService.getByUrl("c")).isEqualTo(Optional.empty());
    }

    @Test
    void getThreadsByNameTest() {
        threadRepository.save(new Thread("test","Test"));
        List<Thread> threads = threadService.getByName("Test");
        assertThat(threads.size()).isEqualTo(1);
        assertThat(threadService.getByName("asdfowaknvian").size()).isZero();
    }

    @Test
    void getByUsernameTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        userRepository.save(new User("user1"));
        userRepository.save(new User("user2"));
        userRepository.save(new User("user3"));

        assertThat(userService.getByUsername("user1")).isPresent();
        assertThat(userService.getByUsername("user4")).isEmpty();
    }

    @Test
    void getTagsOfThreadTest() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        tagRepository.deleteAll();
        threadRepository.deleteAll();

        Thread thread = new Thread("test","test");
        threadRepository.save(thread);
        Tag tag0 = new Tag("tag0",thread);
        Tag tag1 = new Tag("tag1",thread);
        Tag tag2 = new Tag();
        tagRepository.saveAll(Arrays.asList(tag0,tag1,tag2));
        List<Tag> target = tagService.getByThread(thread);
        assertThat(target).contains(tag0);
        assertThat(target).contains(tag1);
        assertThat(target).doesNotContain(tag2);
    }
}
