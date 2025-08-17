package com.example.WoollyProject.domain.auth.service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.auth.entity.BlacklistReason;
import com.example.WoollyProject.domain.auth.entity.BlacklistToken;
import com.example.WoollyProject.domain.auth.repository.BlacklistTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlacklistService {

	private final BlacklistTokenRepository blacklistTokenRepository;

	// 블랙리스트 등록
	public void addToBlacklist(String accessToken, BlacklistReason reason, Long remainingMillis) {
		BlacklistToken token = BlacklistToken.builder()
			.token(accessToken)
			.reason(reason)
			.expiration(remainingMillis)
			.build();

		blacklistTokenRepository.save(token);
	}

	// 블랙리스트 여부 확인
	public boolean isBlacklisted(String accessToken) {
		return blacklistTokenRepository.existsById(accessToken);
	}

	// 사유 조회
	public Optional<BlacklistToken> getBlacklistInfo(String token) {
		return blacklistTokenRepository.findById(token);
	}

}
