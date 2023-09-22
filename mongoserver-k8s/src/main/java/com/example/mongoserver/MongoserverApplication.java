package com.example.mongoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(
		title = "Server API Collection",
		description = "Swagger codegen - Server services APIs for Server operations",
		version = "0.0.1",
		contact = @Contact(email = "sjarunvenkat@gmail.com")
	)
)


@SpringBootApplication
public class MongoserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoserverApplication.class, args);
	}

}
