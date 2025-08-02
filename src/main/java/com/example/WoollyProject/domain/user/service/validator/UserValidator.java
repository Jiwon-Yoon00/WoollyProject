package com.example.WoollyProject.domain.user.service.validator;

import org.springframework.stereotype.Component;

import com.example.WoollyProject.domain.user.repository.UserRepository;
import com.example.WoollyProject.global.exception.CustomException;
import com.example.WoollyProject.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {
	private final UserRepository userRepository;

	public void validateDuplicateEmail(String email) {
		if(userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
		}
	}
}
