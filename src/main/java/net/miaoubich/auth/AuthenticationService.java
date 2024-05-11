package net.miaoubich.auth;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface AuthenticationService {

	void register(@Valid RegistrationRequest request) throws MessagingException;
}
