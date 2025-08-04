package com.example.WoollyProject.global.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.example.WoollyProject.domain.user.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

// JWT 토큰을 생성/검증
// 보통 access token을 만들어주고, 클라이언트가 보낸 JWT의 유효성을 확인하는 핵심 기능을 수행
@Component
@Slf4j
public class JwtProvider {

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long accessExpiration;

	@Value("${jwt.refresh-token-expiration}")
	private long refreshExpiration;

	private SecretKey secretKey;
	private JwtParser jwtParser;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.jwtParser = Jwts.parser().verifyWith(secretKey).build();
	}


	// 토큰 생성
	public String generateAccessToken(String email, Role role) {
		return Jwts.builder()
			.subject(email)
			.claim("email", email)
			.claim("role", role.name())
			.claim("type", "access")
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + accessExpiration))
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(String email) {
		return Jwts.builder()
			.subject(email)
			.claim("email", email)
			.claim("type", "refresh")
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + refreshExpiration))
			.signWith(secretKey)
			.compact();
	}


	// 토큰 검증
	public String getUsername(String token) {
		return jwtParser.parseSignedClaims(token)
			.getPayload()
			.get("email", String.class);
	}

	public String getRole(String token) {
		return jwtParser.parseSignedClaims(token)
			.getPayload()
			.get("role", String.class);
	}

	public Boolean isExpired(String token) {
		return jwtParser.parseSignedClaims(token)
			.getPayload()
			.getExpiration().before(new Date());
	}

	public Boolean isAccessToken(String token) {
		try {
			String type = jwtParser.parseSignedClaims(token)
				.getPayload()
				.get("type", String.class);
			return "access".equals(type);
		} catch (Exception e) {
			return false; // type 클레임 없음 or 파싱 오류
		}
	}


	public Claims getClaims(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	// ?
	// 토큰 유효성
	public boolean validateToken(String token) {
		try {
			jwtParser.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			// 토큰이 만료되었지만 사용자가 재발급 시도 중일 수 있음
			return false;
		} catch (JwtException | IllegalArgumentException e) {
			// 변조된 토큰
			return false;
		}
	}
}
