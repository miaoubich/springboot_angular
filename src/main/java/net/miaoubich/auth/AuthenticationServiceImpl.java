package net.miaoubich.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.miaoubich.email.EmailService;
import net.miaoubich.email.EmailTemplateName;
import net.miaoubich.repository.RoleRepository;
import net.miaoubich.repository.TokenRepository;
import net.miaoubich.repository.UserRepository;
import net.miaoubich.security.JwtService;
import net.miaoubich.user.Token;
import net.miaoubich.user.User;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final EmailService emailService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	
	@Value("${application.mailing.frontend.activation-url}")
	private String activationUrl;

	@Override
	public void register(@Valid RegistrationRequest request) throws MessagingException {
		var userRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Role User wasn't initialized."));
		var user = User.builder()
				.firstname(request.getFirstname())
				.lastname(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.accountLocked(false)
				.enabled(false)
				.roles(List.of(userRole))
				.build();
		userRepository.save(user);

		sendValidationEmail(user);
	}

	private void sendValidationEmail(User user) throws MessagingException {
		String newToken = generateAndSaveActivationToken(user);

		// send Email
		emailService.sendEMail(user.getEmail(), 
				               user.getFullName(), 
				               EmailTemplateName.ACTIVATE_ACCOUNT, 
				               activationUrl,
				               newToken, 
				               "Account activation"
				               );
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

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getEmail(),
						request.getPassword()));
		var claims = new HashMap<String, Object>();
		var user = ((User) auth.getPrincipal());
		claims.put("fullname", user.getFullName());
		var jwtToken = jwtService.generateToken(claims, user);
		
		return AuthenticationResponse.builder()
									 .token(jwtToken)
									 .build();
	}

//	@Transactional
	@Override
	public void activateAccount(String token) {
		Token savedToken = tokenRepository.findByToken(token)
						.orElseThrow(() -> new RuntimeException("Invalid token."));
		if(LocalDateTime.now().isAfter((savedToken.getExpiredAt()))){
			try {
				sendValidationEmail(savedToken.getUser());
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			throw new RuntimeException("Activation token has expired. "
					+ "A new token has been sent to the same email address.");
		}
		var user = userRepository.findById(savedToken.getUser().getId())
						.orElseThrow(()-> new UsernameNotFoundException("User not found."));
		user.setEnabled(true);
		userRepository.save(user);
		savedToken.setValidatedAt(LocalDateTime.now());
		tokenRepository.save(savedToken);
	}

}
