package com.example.WoollyProject.domain.auth.service;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.auth.dto.response.TokenResDto;
import com.example.WoollyProject.domain.auth.entity.RefreshTokenRedis;
import com.example.WoollyProject.domain.auth.repository.RefreshTokenRepository;
import com.example.WoollyProject.domain.user.entity.Role;
import com.example.WoollyProject.global.exception.CustomException;
import com.example.WoollyProject.global.exception.ErrorCode;
import com.example.WoollyProject.global.security.JwtProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final JwtProvider jwtProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@Transactional
	public TokenResDto refreshToken(String oldRefreshToken) {

		// 토큰 검증 - 만료되었는지
		if (jwtProvider.isExpired(oldRefreshToken)) {
			throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
		}

		// 토큰이 refresh인지 확인
		if(!jwtProvider.isRefreshToken(oldRefreshToken)){
			throw new CustomException(ErrorCode.ACCESS_TOKEN_EXPIRED);
		}

		// 사용자 정보 추출
		String email = jwtProvider.getUsername(oldRefreshToken);
		Role role = jwtProvider.getRole(oldRefreshToken);

		// Redis에서 기존 토큰 조회
		RefreshTokenRedis existingToken = refreshTokenRepository.findById(email)
			.orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

		// 토큰 일치 여부 확인
		if (!existingToken.getRefresh().equals(oldRefreshToken)) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}

		// 토큰 rotation: 기존 토큰 삭제
		refreshTokenRepository.deleteById(email);

		// accessToken 재발급
		String newAccessToken = jwtProvider.generateAccessToken(email, role);
		String newRefreshToken = jwtProvider.generateRefreshToken(email, role);

		// 새 refreshToken 저장
		addRefreshEntity(email, newRefreshToken, 86400000L);

		return new TokenResDto(newAccessToken, newRefreshToken);
	}

	public void addRefreshEntity(String email, String refresh, Long expiredMs) {
		RefreshTokenRedis token = RefreshTokenRedis.builder()
			.email(email)
			.refresh(refresh)
			.expiration(System.currentTimeMillis() + expiredMs)
			.build();

		refreshTokenRepository.save(token);
	}
}
