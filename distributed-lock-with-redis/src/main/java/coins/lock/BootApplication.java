package coins.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BootApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(BootApplication.class, args);
	}
}
