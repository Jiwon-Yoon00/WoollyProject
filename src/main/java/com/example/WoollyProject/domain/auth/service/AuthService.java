package com.example.WoollyProject.domain.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.auth.dto.response.TokenResDto;
import com.example.WoollyProject.domain.user.entity.Role;
import com.example.WoollyProject.global.dto.ApiRes;
import com.example.WoollyProject.global.exception.CustomException;
import com.example.WoollyProject.global.exception.ErrorCode;
import com.example.WoollyProject.global.security.JwtProvider;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtProvider jwtProvider;

	public TokenResDto refreshToken(String refreshToken) {

		// 토큰 검증 - 만료되었는지
		if (jwtProvider.isExpired(refreshToken)) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}

		// 토큰이 refresh인지 확인
		if(!jwtProvider.isRefreshToken(refreshToken)){
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}

		// 사용자 정보 추출
		String email = jwtProvider.getUsername(refreshToken);
		Role role = jwtProvider.getRole(refreshToken);

		// accessToken 재발급
		String newAccessToken = jwtProvider.generateAccessToken(email, role);
		String newRefreshToken = jwtProvider.generateRefreshToken(email, role);

		return new TokenResDto(newAccessToken, newRefreshToken);
	}
}
