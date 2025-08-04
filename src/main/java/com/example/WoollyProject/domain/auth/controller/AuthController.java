package com.example.WoollyProject.domain.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.WoollyProject.domain.auth.dto.response.TokenResDto;
import com.example.WoollyProject.domain.auth.service.AuthService;
import com.example.WoollyProject.global.dto.ApiRes;
import com.example.WoollyProject.global.exception.CustomException;
import com.example.WoollyProject.global.exception.ErrorCode;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
	private  final AuthService authService;

	@GetMapping("/refresh")
	public ResponseEntity<ApiRes<TokenResDto>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		// 1. 쿠키에서 refresh token 추출
		String refreshToken = extractRefreshTokenFromCookie(request);

		if (refreshToken == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiRes.fail("Refresh token is missing."));
		}

		try {

			TokenResDto tokenResDto  = authService.refreshToken(refreshToken);

			// refreshToken
			Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResDto.getRefreshToken());
			refreshTokenCookie.setHttpOnly(true);
			//refreshTokenCookie.setSecure(true); // HTTPS 환경일 경우
			refreshTokenCookie.setMaxAge(7*24*60*60); // 7일
			refreshTokenCookie.setPath("/");

			response.setHeader("Authorization", "Bearer " + tokenResDto.getAccessToken());
			response.addCookie(refreshTokenCookie);

			return ResponseEntity.ok(ApiRes.ok(tokenResDto ));

		} catch (CustomException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiRes.fail(ErrorCode.UNAUTHORIZED));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiRes.fail(ErrorCode.INTERNAL_SERVER_ERROR));
		}
	}

	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) return null;

		for (Cookie cookie : cookies) {
			if ("refreshToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}
}
