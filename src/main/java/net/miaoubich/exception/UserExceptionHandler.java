package net.miaoubich.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

	@ExceptionHandler(UserCustomException.class)
	public ResponseEntity<ErrorMessage> userExceptionHandler(UserCustomException exception){
		return new ResponseEntity<>(
				ErrorMessage.builder()
					.errorMassage(exception.getMessage())
					.errorCode(exception.getErrorCode())
					.build(), exception.getStatus()
				);
	}
}
