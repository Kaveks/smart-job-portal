package com.kaveks.userservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {

		// Load .env when available (local development)
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
				.ignoreIfMissing()
				.load();

		// Shared PostgreSQL credentials
		setPropertyIfMissing("DB_USERNAME", dotenv.get("DB_USERNAME"));
		setPropertyIfMissing("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

		// User Service configuration
		setPropertyIfMissing("USER_DB_URL", dotenv.get("USER_DB_URL"));
		setPropertyIfMissing("USER_SERVICE_PORT", dotenv.get("USER_SERVICE_PORT"));

		// Shared application configuration
		setPropertyIfMissing("JPA_DDL_AUTO", dotenv.get("JPA_DDL_AUTO"));
		setPropertyIfMissing("CONFIG_SERVER_URL", dotenv.get("CONFIG_SERVER_URL"));
		setPropertyIfMissing("EUREKA_SERVER_URL", dotenv.get("EUREKA_SERVER_URL"));

		SpringApplication.run(UserserviceApplication.class, args);
	}

	private static void setPropertyIfMissing(String key, String value) {
		if (System.getenv(key) == null
				&& System.getProperty(key) == null
				&& value != null) {
			System.setProperty(key, value);
		}
	}
}