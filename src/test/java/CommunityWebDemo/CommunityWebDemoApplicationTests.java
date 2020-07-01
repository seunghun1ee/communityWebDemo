package CommunityWebDemo;

import CommunityWebDemo.entity.Post;
import CommunityWebDemo.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityWebDemoApplicationTests {

	FakeDB fakeDB = new FakeDB();

	@Autowired
	PostService postService;

	@Test
	void fakeDBInit() {
		assert (fakeDB.postTable.isEmpty());
	}

	@Test
	void addPostTest() {
		Post post0 = new Post((long) 0,"test title","test body", (long) 0);
		postService.addPost(fakeDB, post0);
		assert (!fakeDB.postTable.isEmpty());
		fakeDB.postTable.clear();
	}

	@Test
	void getAllPostTest() {
		Post post0 = new Post((long) 0,"test title","test body", (long) 0);
		Post post1 = new Post((long) 1,"test title","test body", (long) 1);
		postService.addPost(fakeDB, post0);
		postService.addPost(fakeDB, post1);
		assert (!postService.getAllPosts(fakeDB).isEmpty());
		assert (postService.getAllPosts(fakeDB).size() == 2);
		fakeDB.postTable.clear();
	}

	@Test
	void contextLoads() {
	}

}
