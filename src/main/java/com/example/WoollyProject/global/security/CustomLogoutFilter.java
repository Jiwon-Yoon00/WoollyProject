package com.example.WoollyProject.global.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.example.WoollyProject.domain.auth.repository.RefreshRepository;
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
public class CustomLogoutFilter extends GenericFilterBean {
	private final JwtProvider jwtProvider;
	private final RefreshRepository refreshRepository;
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
		String requestUri = request.getRequestURI();
		if (!requestUri.matches("^\\/api/v1/auth/logout$")) {

			filterChain.doFilter(request, response);
			return;
		}
		String requestMethod = request.getMethod();
		if (!requestMethod.equals("POST")) {

			filterChain.doFilter(request, response);
			return;
		}

		//get refresh token
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refreshToken")) {

				refresh = cookie.getValue();
			}
		}

		//refresh null check
		if (refresh == null) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//expired check
		try {
			jwtProvider.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		if (!jwtProvider.isRefreshToken(refresh)) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//DB에 저장되어 있는지 확인
		Boolean isExist = refreshRepository.existsByRefresh(refresh);
		if (!isExist) {

			//response status code
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		//로그아웃 진행
		//Refresh 토큰 DB에서 제거
		refreshRepository.deleteByRefresh(refresh);

		//Refresh 토큰 Cookie 값 0
		Cookie cookie = new Cookie("refresh", null);
		cookie.setMaxAge(0);
		cookie.setPath("/");

		response.addCookie(cookie);

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);//path and method verify

		ApiRes<String> apiRes = ApiRes.ok();
		objectMapper.writeValue(response.getWriter(), apiRes);

	}
}
