package com.testcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@SpringBootApplication
public class ExampleApplication implements CommandLineRunner {

	private final RedisTemplate<String, String> redisTemplate;

	@Autowired
	public ExampleApplication(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String message = "Hello, Redis!";
		redisTemplate.delete("messages"); // Clear the "messages" list
		redisTemplate.opsForList().rightPush("messages", message);

		List<String> messages = redisTemplate.opsForList().range("messages", 0, -1);
		System.out.println("Messages in Redis:");
		for (String msg : messages) {
			System.out.println(msg);
		}
	}
}