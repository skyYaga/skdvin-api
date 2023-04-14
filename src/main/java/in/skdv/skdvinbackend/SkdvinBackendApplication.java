package in.skdv.skdvinbackend;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongock
@EnableScheduling
//@Configuration
//@Import({ApplicationConfig.class, SecurityConfig.class, EmailConfig.class, Auth0Config.class})
public class SkdvinBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkdvinBackendApplication.class, args);
	}
}
