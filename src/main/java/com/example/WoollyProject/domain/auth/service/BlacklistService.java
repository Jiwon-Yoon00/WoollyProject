package com.example.WoollyProject.domain.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlacklistService {
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String PREFIX = "BL:";

	// 블랙리스트 등록
	public void addToBlacklist(String accessToken, long remainingMillis) {
		redisTemplate.opsForValue().set(
			PREFIX + accessToken,
			"logout",
			Duration.ofMillis(remainingMillis)
		);
	}

	// 블랙리스트 여부 확인
	public boolean isBlacklisted(String accessToken) {
		return redisTemplate.hasKey(PREFIX + accessToken);
	}
}
