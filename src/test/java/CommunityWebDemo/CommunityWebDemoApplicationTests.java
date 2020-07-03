package CommunityWebDemo;

import CommunityWebDemo.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityWebDemoApplicationTests {

	@Autowired
	PostService postService;

	@Test
	void contextLoads() {
	}

}
