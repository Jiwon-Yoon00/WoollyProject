package com.example.WoollyProject.global.security;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.WoollyProject.domain.user.entity.Role;
import com.example.WoollyProject.global.auth.dto.request.LoginReqDto;
import com.example.WoollyProject.global.dto.ApiRes;
import com.example.WoollyProject.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final ObjectMapper objectMapper; // json과 객체간의 변환을 담당, LocalDateTime 직렬화 문제

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
		throws AuthenticationException {

		try{
			LoginReqDto reqDto = objectMapper.readValue(request.getInputStream(), LoginReqDto.class);

			String email = reqDto.getEmail();
			String password = reqDto.getPassword();

			// 일종의 dto
			UsernamePasswordAuthenticationToken token =
				new UsernamePasswordAuthenticationToken(email, password, null);

			// token에 담은 검증을 위한 AuthenticationManager에게 넘겨줌
			return authenticationManager.authenticate(token);
		}catch(Exception e){
			throw new AuthenticationServiceException(e.getMessage());
		}
	}

	// 로그인 성공 시 실행하는 메소드 - jwt 발급
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws
		IOException {
		log.info("💡 로그인 성공");

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String email = userDetails.getUsername();
		Long userId = userDetails.getUserId();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		Role role = Role.valueOf(auth.getAuthority());


		String accessToken = jwtProvider.generateAccessToken(email, role);
		String refreshToken = jwtProvider.generateRefreshToken(email);

		// accessToken
		response.setHeader("Authorization", "Bearer " + accessToken);

		// refreshToken
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(true);
		//refreshTokenCookie.setSecure(true); // HTTPS 환경일 경우
		refreshTokenCookie.setMaxAge(7*24*60*60); // 7일
		refreshTokenCookie.setPath("/");
		response.addCookie(refreshTokenCookie);

		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("role", role);
		map.put("email", email);

		ApiRes<Map<String, Object>> apiRes = ApiRes.ok(map);

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);

		objectMapper.writeValue(response.getWriter(), apiRes);
	}


	// 로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws
		IOException {
		log.info("❌ 로그인 실패");
		// 예: "이메일 또는 비밀번호가 잘못되었습니다"
		ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // 기본값

		// if (failed instanceof BadCredentialsException) {
		// 	errorCode = ErrorCode.LOGIN_FAILED; // 자격 증명 실패
		// } else if (failed instanceof UsernameNotFoundException) {
		// 	errorCode = ErrorCode.USER_NOT_FOUND; // 아이디 없음
		// } else if (failed instanceof DisabledException) {
		// 	errorCode = ErrorCode.ACCOUNT_DISABLED; // 비활성화 계정
		// } else if (failed instanceof LockedException) {
		// 	errorCode = ErrorCode.ACCOUNT_LOCKED; // 잠금 계정
		// }

		ApiRes<?> errorResponse = ApiRes.fail(errorCode);

		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401

		objectMapper.writeValue(response.getWriter(), errorResponse);

	}
}
