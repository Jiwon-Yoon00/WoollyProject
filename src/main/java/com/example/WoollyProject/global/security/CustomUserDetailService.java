package com.example.WoollyProject.global.security;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.user.entity.User;
import com.example.WoollyProject.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
	private final UserRepository userRepository;

	// 사용자의 이름(또는 ID, 이메일 등)을 입력받아 데이터베이스 등에서 사용자를 검색
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);

		if(user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다. " + username);
		}

		return new CustomUserDetails(user);
	}
}
