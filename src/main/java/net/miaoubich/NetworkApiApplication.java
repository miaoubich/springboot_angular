package net.miaoubich;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkApiApplication.class, args);
	}

}
