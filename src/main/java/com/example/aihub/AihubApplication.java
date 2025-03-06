package com.example.aihub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.aihub.mapper")
public class AihubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AihubApplication.class, args);
	}

}
