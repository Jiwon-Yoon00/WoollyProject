package com.example.WoollyProject.global.dto;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.example.WoollyProject.global.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiRes<T> {

	private int code;
	private boolean success;
	private String message;
	private T data;
	private LocalDateTime time;

	// 기본 성공 응답 (code: 200, message: "요청이 성공했습니다.")
	public static<T> ApiRes<T> ok(T data) {
		return new ApiRes<>(200, true, "요청이 성공했습니다.", data, LocalDateTime.now());
	}

	// 실패 응답
	public static <T> ApiRes<T> fail(ErrorCode errorCode) {
		return new ApiRes<>(
			errorCode.getHttpStatus().value(),
			false,
			errorCode.getMessage(),
			null,
			LocalDateTime.now()
		);
	}

	// 실패 응답
	public static <T> ApiRes<T> fail(ErrorCode errorCode, T data) {
		return new ApiRes<>(
			errorCode.getHttpStatus().value(),
			false,
			errorCode.getMessage(),
			data,
			LocalDateTime.now()
		);
	}

	// 실패 응답 (에러코드 없이 메시지만)
	public static <T> ApiRes<T> fail(String message) {
		return new ApiRes<>(
			HttpStatus.BAD_REQUEST.value(),
			false,
			message,
			null,
			LocalDateTime.now()
		);
	}

	// 커스텀 메세지 + 데이터
	public static <T> ApiRes<T> fail(String message, T data) {
		return new ApiRes<>(
			HttpStatus.BAD_REQUEST.value(),
			false,
			message,
			data,
			LocalDateTime.now()
		);
	}



}
