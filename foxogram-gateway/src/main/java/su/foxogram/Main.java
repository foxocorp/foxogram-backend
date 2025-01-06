package su.foxogram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Main.class);
		app.setDefaultProperties(Map.of("server.port", "8081"));
		app.run(args);
	}
}
