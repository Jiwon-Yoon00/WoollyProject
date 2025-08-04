package com.example.WoollyProject.global.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.WoollyProject.domain.user.entity.User;
import com.example.WoollyProject.global.dto.ApiRes;
import com.example.WoollyProject.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
*
* Http 요청이 들어올 때마다 가장 먼저 실행되어,
* HTTP 헤더에서 JWT 토큰을 추출
* JwtProvider로 토큰 유효성 검증
* 유효한 경우, 인증 정보를 SecurityContext에 저장
*
* */
//HTTP헤더에서 토큰 추출
// jwtProvider로 토큰 유효성 검증
@Component
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private JwtProvider jwtProvider;
	private CustomUserDetailService customUserDetailService;
	private ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String header = request.getHeader("access");

		//Authorization 헤더 검증
		if(header == null || !header.startsWith("Bearer ")){
			log.error("Authorization header not found");
			filterChain.doFilter(request, response);
			return;
		}

		String token = header.replace("Bearer ", "");

		// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
		try {
			jwtProvider.isExpired(token);
		} catch (ExpiredJwtException e) {
			sendErrorResponse(response, ErrorCode.ACCESS_TOKEN_EXPIRED);
			return;
		}

		// 토큰이 accessToken인지
		if(!jwtProvider.isAccessToken(token)){
			sendErrorResponse(response, ErrorCode.INVALID_ACCESS_TOKEN);
			return;
		}

		// 토큰에서 사용자 정보 추출
		String email = jwtProvider.getUsername(token);

		// 사용자 정보 조회
		UserDetails userDetails = customUserDetailService.loadUserByUsername(email);

		// 인증 객체 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		// 시큐리티컨텍스트에 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		filterChain.doFilter(request, response);
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType("application/json; charset=UTF-8");

		ApiRes<?> errorResponse = ApiRes.fail(errorCode);

		String json = objectMapper.writeValueAsString(errorResponse);

		PrintWriter writer = response.getWriter();
		writer.print(json);
		writer.flush();
	}

}
