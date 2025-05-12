package ru.roms2002.tokenviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TokenViewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TokenViewerApplication.class, args);
	}

}
