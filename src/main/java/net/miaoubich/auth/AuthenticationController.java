package net.miaoubich.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication")
@Slf4j
public class AuthenticationController {

	private final AuthenticationServiceImpl authenticationService;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException{
		log.info("Inside register new USER api call, and before the call it executed!");
		authenticationService.register(request);
		log.info("After executing the api call for registering a new USER!");
		
		return ResponseEntity.accepted().build();
	}
	
	//login end-point
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request){
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}
	
	@GetMapping("/activate-account")
	public void confirm(@RequestParam String token) {
		authenticationService.activateAccount(token);
	}
 }

