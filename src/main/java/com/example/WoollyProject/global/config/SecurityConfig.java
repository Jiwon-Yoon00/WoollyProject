package com.example.WoollyProject.global.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // CSRF 비활성화 예시
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/", "/login", "/signup").permitAll()  // 누구나 접근 가능
				.requestMatchers("/admin/**").hasRole("ADMIN")          // ADMIN 권한 필요
				.anyRequest().authenticated()                           // 나머지 요청은 인증 필요
			)
			.formLogin(withDefaults())  // 기본 로그인 폼 사용 설정
			.httpBasic(withDefaults()); // HTTP Basic 인증 활성화

		return http.build();
	}
}
