package com.example.WoollyProject.domain.user.dto.response;

import com.example.WoollyProject.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupResDto {
	private String nickname;
	private String email;

	public static SignupResDto from(User user) {
		return new SignupResDto(
			user.getNickname(),
			user.getEmail()
		);
	}
}
