package CommunityWebDemo;

import CommunityWebDemo.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CommunityWebDemoApplication {

	@Autowired
	PostRepository postRepository;

	public static void main(String[] args) {
		SpringApplication.run(CommunityWebDemoApplication.class, args);
	}

}
