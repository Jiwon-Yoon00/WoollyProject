package com.example.WoollyProject.domain.auth.entity;

import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@RedisHash("blacklist")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistToken {

	@Id
	private String token;

	private BlacklistReason reason;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private Long expiration; // Redis가 자동으로 만료시키는 TTL
}
