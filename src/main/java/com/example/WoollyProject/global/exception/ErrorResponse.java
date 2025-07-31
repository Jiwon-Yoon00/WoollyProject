package com.example.WoollyProject.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class ErrorResponse {
	private HttpStatus code;
	private String message;
	private LocalDateTime timestamp;

	public static ErrorResponse of(HttpStatus code, String message) {
		return new ErrorResponse(code, message, LocalDateTime.now());
	}
}
