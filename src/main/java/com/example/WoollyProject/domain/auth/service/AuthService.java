package com.example.WoollyProject.domain.auth.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.auth.dto.response.TokenResDto;
import com.example.WoollyProject.domain.auth.entity.RefreshEntity;
import com.example.WoollyProject.domain.auth.repository.RefreshRepository;
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
	private final RefreshRepository refreshRepository;

	public TokenResDto refreshToken(String refreshToken) {

		// 토큰 검증 - 만료되었는지
		if (jwtProvider.isExpired(refreshToken)) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}

		// 토큰이 refresh인지 확인
		if(!jwtProvider.isRefreshToken(refreshToken)){
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}

		Boolean isExist = refreshRepository.existsByRefresh(refreshToken);
		if (!isExist) {
			//response body
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		// 사용자 정보 추출
		String email = jwtProvider.getUsername(refreshToken);
		Role role = jwtProvider.getRole(refreshToken);

		// accessToken 재발급
		String newAccessToken = jwtProvider.generateAccessToken(email, role);
		String newRefreshToken = jwtProvider.generateRefreshToken(email, role);

		refreshRepository.deleteByRefresh(refreshToken);
		addRefreshEntity(email, newRefreshToken, 86400000L);

		return new TokenResDto(newAccessToken, newRefreshToken);
	}

	private void addRefreshEntity(String email, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		RefreshEntity refreshEntity = new RefreshEntity();
		refreshEntity.setEmail(email);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshRepository.save(refreshEntity);
	}
}
