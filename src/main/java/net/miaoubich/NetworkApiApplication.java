package net.miaoubich;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import net.miaoubich.repository.RoleRepository;
import net.miaoubich.role.Role;
import net.miaoubich.security.JwtService;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class NetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkApiApplication.class, args);
	}
	
	@Bean
	public JwtService jwtService() {
	    return new JwtService();
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder()
										.name("USER")
										.build());
			}
		};
	}
}
