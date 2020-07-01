package CommunityWebDemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityWebDemoApplicationTests {

	FakeDB fakeDB = new FakeDB();

	@Test
	void fakeDBInit() {
		assert (fakeDB.postTable.isEmpty());
	}

	@Test
	void contextLoads() {
	}

}
