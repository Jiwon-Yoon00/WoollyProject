package com.example.WoollyProject.global.security;

import java.io.IOException;
import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.example.WoollyProject.domain.auth.entity.BlacklistReason;
import com.example.WoollyProject.domain.auth.repository.RefreshRepository;
import com.example.WoollyProject.domain.auth.repository.RefreshTokenRepository;
import com.example.WoollyProject.domain.auth.service.BlacklistService;
import com.example.WoollyProject.global.dto.ApiRes;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
@Order(0)
public class CustomLogoutFilter extends GenericFilterBean {
	private final JwtProvider jwtProvider;
	//private final RefreshRepository refreshRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlacklistService blacklistService;
	private final ObjectMapper objectMapper;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
		IOException,
		ServletException {
		doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
	}

	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		IOException, ServletException {


		//path and method verify
		System.out.println("request URI: " + request.getRequestURI());
		if (!"/api/v1/auth/logout".equals(request.getRequestURI())) {
			filterChain.doFilter(request, response);
			return;
		}


		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}
		System.out.println("request method: " + requestMethod);

		//get refresh token
		String refreshToken = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null) {
			for (Cookie cookie : cookies) {

				if (cookie.getName().equals("refreshToken")) {

					refreshToken = cookie.getValue();
				}
			}
		}

		//refresh null check
		if (refreshToken == null) {
			errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token is missing");
			return;
		}

		String email = jwtProvider.getUsername(refreshToken);

		//expired check
		try {
			jwtProvider.isExpired(refreshToken);
		} catch (ExpiredJwtException e) {
			errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token is expired");
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		if (!jwtProvider.isRefreshToken(refreshToken)) {

			//response status code
			errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid token type");
			return;
		}

		//DB에 저장되어 있는지 확인
		//Boolean isExist = refreshRepository.existsByRefresh(refresh);
		boolean isExist = refreshTokenRepository.existsById(email);
		if (!isExist) {

			//response status code
			errorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Refresh token not found in DB");
			return;
		}

		//로그아웃 진행
		//Refresh 토큰 DB에서 제거
		//refreshRepository.deleteByRefresh(refresh);
		System.out.println("Email: " + email);
		refreshTokenRepository.deleteById(email);


		//Refresh 토큰 Cookie 값 0
		Cookie cookie = new Cookie("refreshToken", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		// AccessToken 블랙리스트 등록
		String accessToken = resolveAccessToken(request);
		if (accessToken != null && !accessToken.isEmpty()) {
			//System.out.println("블랙리스트");
			long remainingMillis = getRemainingMillis(accessToken);
			blacklistService.addToBlacklist(accessToken, BlacklistReason.LOGOUT ,remainingMillis);
		}


		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);//path and method verify

		ApiRes<String> apiRes = ApiRes.ok();
		objectMapper.writeValue(response.getWriter(), apiRes);

	}

	private void errorResponse(HttpServletResponse response, int status, String message) throws IOException{
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(status);
		ApiRes<String> apiRes = ApiRes.fail(message);
		objectMapper.writeValue(response.getWriter(), apiRes);
	}

	public String resolveAccessToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization"); // "Bearer <token>"
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public long getRemainingMillis(String token) {
		Date expiration = jwtProvider.getClaims(token).getExpiration();
		return expiration.getTime() - System.currentTimeMillis();
	}

}
