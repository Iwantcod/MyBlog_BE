package com.example.MyBlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MyBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyBlogApplication.class, args);
	}

}
