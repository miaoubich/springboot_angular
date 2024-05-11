package net.miaoubich.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.miaoubich.email.EmailService;
import net.miaoubich.email.EmailTemplateName;
import net.miaoubich.repository.RoleRepository;
import net.miaoubich.repository.TokenRepository;
import net.miaoubich.repository.UserRepository;
import net.miaoubich.user.Token;
import net.miaoubich.user.User;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository UuerRepository;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;

	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;

	@Override
	public void register(@Valid RegistrationRequest request) throws MessagingException {
		var userRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Role User wasn't initialized."));
		var user = User.builder()
				.firstname(request.getFirsname())
				.lastname(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.accountLocked(false)
				.enabled(false)
				.roles(List.of(userRole)).build();
		UuerRepository.save(user);

		sendValidationEmail(user);
	}

	private void sendValidationEmail(User user) throws MessagingException {
		String newToken = generateAndSaveActivationToken(user);

		// send Email
		emailService.sendEMail(user.getEmail(), user.getFullName(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl,
				newToken, "Account activation");
	}

	private String generateAndSaveActivationToken(User user) {
		// generate a token
		String generatedToken = generateActivationCode(6);// length of the pin is 6
		var token = Token.builder().
				token(generatedToken)
				.createdAt(LocalDateTime.now())
				.expiredAt(LocalDateTime.now().plusMinutes(20))
				.user(user)
				.build();
		tokenRepository.save(token);

		return generatedToken;
	}

	// Generate an activation code PIN of 6 digits
	private String generateActivationCode(int length) {
		String characters = "0123456789";
		StringBuilder sb = new StringBuilder();
		SecureRandom secureRandom = new SecureRandom();

		for (int i = 0; i < length; i++) {
			int randomIndex = secureRandom.nextInt(characters.length());// 0..9
			sb.append(characters.charAt(randomIndex));
		}

		return sb.toString();
	}

}
