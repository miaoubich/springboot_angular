package net.miaoubich.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequest {

	@NotEmpty(message = "Firstname is mandatory.")
	@NotBlank(message = "Firstname is mandatory.")
	private String firsname;
	@NotBlank(message = "Lastname is mandatory.")
	@NotBlank(message = "Lastname is mandatory.")
	private String lastname;
	@NotBlank(message = "Email is mandatory.")
	@NotBlank(message = "Email is mandatory.")
	@Email(message = "Email is not formatted.")
	private String email;
	@NotBlank(message = "Password is mandatory.")
	@NotBlank(message = "Password is mandatory.")
	@Size(min = 8, message = "Password should be minimum 8 characters.")
	private String password;
}
