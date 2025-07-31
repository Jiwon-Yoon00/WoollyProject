package com.example.WoollyProject.global.exception;

import static com.example.WoollyProject.global.exception.ErrorCode.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.WoollyProject.global.dto.ApiRes;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiRes<Void>> handleCustomException(CustomException e) {
		ErrorCode errorCode = e.getErrorCode();

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiRes.fail(errorCode));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiRes<Void>> handleException(Exception exception) {
		log.error("Unhandled Exception", exception);

		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(ApiRes.fail(errorCode));
	}
}
