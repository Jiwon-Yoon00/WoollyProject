package com.example.WoollyProject.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 400 BAD REQUEST
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력입니다."),
	MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 항목이 누락되었습니다."),

	// 401 UNAUTHORIZED
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

	// 403 FORBIDDEN
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

	// 404 NOT FOUND
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

	// 409 CONFLICT
	DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "이미 존재하는 리소스입니다."),

	// 500 INTERNAL SERVER ERROR
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus; // HTTP 상태 코드
	private final String message;      // 에러 메시지

}
