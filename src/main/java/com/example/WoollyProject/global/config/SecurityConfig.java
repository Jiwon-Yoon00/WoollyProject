package com.example.WoollyProject.global.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.example.WoollyProject.global.security.CustomUserDetailService;
import com.example.WoollyProject.global.security.JwtAuthenticationFilter;
import com.example.WoollyProject.global.security.JwtProvider;
import com.example.WoollyProject.global.security.LoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final JwtProvider jwtProvider;
	private final CustomUserDetailService customUserDetailService;
	private final ObjectMapper objectMapper;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration),  jwtProvider, objectMapper);
		loginFilter.setFilterProcessesUrl("/api/v1/users/login");

		http
			.csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화, jwt 방식은 csrf에 대한 공격을 방어하지 않아도 됨
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)

			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/api/v1/users/login", "/api/v1/users/signup").permitAll()  // 누구나 접근 가능
				.requestMatchers("/admin/**").hasRole("ADMIN")          // ADMIN 권한 필요
				.anyRequest().authenticated()                           // 나머지 요청은 인증 필요
			)

			.addFilterBefore(new JwtAuthenticationFilter(jwtProvider, customUserDetailService ), LoginFilter.class)
			.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)

			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 설정
		;

		return http.build();
	}
}
