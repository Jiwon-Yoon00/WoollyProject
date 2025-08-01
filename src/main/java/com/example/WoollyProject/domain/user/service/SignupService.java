package com.example.WoollyProject.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.WoollyProject.domain.user.dto.request.SignupReqDto;
import com.example.WoollyProject.domain.user.dto.response.SignupResDto;
import com.example.WoollyProject.domain.user.entity.User;
import com.example.WoollyProject.domain.user.repository.UserRepository;
import com.example.WoollyProject.domain.user.service.validator.UserValidator;
import com.example.WoollyProject.global.exception.CustomException;
import com.example.WoollyProject.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SignupService {

	private final UserRepository userRepository;
	private final UserValidator userValidator;
	private final PasswordEncoder passwordEncoder;

	public SignupResDto signup(SignupReqDto signupReqDto) {
		// 1. 중복 체크
		userValidator.validateDuplicateEmail(signupReqDto.getEmail());

		// 2. 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(signupReqDto.getPassword());

		//3. 유저 생성 및 저장
		User user = signupReqDto.toEntity(encodedPassword);
		userRepository.save(user);

		return SignupResDto.from(user);
	}
}
