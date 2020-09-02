package CommunityWebDemo;

import CommunityWebDemo.entity.Thread;
import CommunityWebDemo.repository.ThreadRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class CommunityWebDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityWebDemoApplication.class, args);
	}

	@Bean
	public ApplicationRunner loadInitialThreads(ThreadRepository repository) {
		return args -> repository.saveAll(Arrays.asList(
				new Thread("random","Random","You can say anything is this thread"),
				new Thread("bris","Bristol","Talk about Bristol things"),
				new Thread("pol","Politics","Share your political opinion"),
				new Thread("earth","Planet Earth","Share things about our beautiful planet Earth"),
				new Thread("game","Video Games","Are ya winning son?"),
				new Thread("music","Music","Talk about any kind of music you like"),
				new Thread("vroom","Motorsports","It's lights out and away we go! Hamilton gets an excellent start! Lewis Hamilton! Champion of the world!!!"),
				new Thread("fball","Football","Talk about football"),
				new Thread("HnF","Health & Fitness","Share your workout routines"),
				new Thread("beep","Computer Science","Code\nCompile\nError\nDebug\nRepeat"),
				new Thread("help","I Need Help","When you need some assistance or advice"),
				new Thread("aww","Aww","Share things that make you go \"Aww...\""),
				new Thread("meme","MEME","Share Dank memes *Air horn noise*"),
				new Thread("baking","Baking","Talk about baking"),
				new Thread("bug","Bug Report","Please leave bug reports here during testing")
		));
	}

}
