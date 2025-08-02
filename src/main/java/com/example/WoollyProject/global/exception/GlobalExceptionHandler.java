package com.example.WoollyProject.global.exception;

import static com.example.WoollyProject.global.exception.ErrorCode.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.WoollyProject.global.dto.ApiRes;
import com.example.WoollyProject.global.dto.FieldErrorDto;

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

	@ExceptionHandler(MethodArgumentNotValidException.class) // @Valid가 실패했을 때 자동으로 던지는 예외 클래스
	public ResponseEntity<ApiRes<List<FieldErrorDto>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		List<FieldErrorDto> errors = e.getBindingResult().getFieldErrors().stream()
			.map(error -> new FieldErrorDto(error.getField(), error.getDefaultMessage()))
			.collect(Collectors.toList());

		String topMessage = errors.stream()
			.filter(err -> err.field().equals("confirmPassword"))
			.map(FieldErrorDto::message)
			.findFirst()
			.orElse(errors.get(0).message());

		return ResponseEntity.badRequest().body(ApiRes.fail(topMessage, errors));

		// FieldError fieldError = e.getFieldErrors().get(0);
		// String errorMessage = fieldError.getDefaultMessage();
		//
		// String code = fieldError.getCode(); // 어떤 어노테이션이 적용되었는 지 확인
		// ErrorCode errorCode;
		//
		// // 필수값이 비어있으면 MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 항목이 누락되었습니다.")
		// // 형식 또는 조건 위반이라면 INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다.")
		// if (code != null && (
		// 	code.equals("NotNull") ||
		// 		code.equals("NotBlank") ||
		// 		code.equals("NotEmpty")
		// )) {
		// 	errorCode = ErrorCode.MISSING_REQUIRED_FIELD;
		// } else {
		// 	errorCode = ErrorCode.INVALID_INPUT_VALUE;
		// }
		//
		// return ResponseEntity
		// 	.status(errorCode.getHttpStatus())
		// 	.body(ApiRes.fail(errorMessage)); // 메시지는 사용자에게 직접 전달
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
