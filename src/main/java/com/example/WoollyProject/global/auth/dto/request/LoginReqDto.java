package com.example.WoollyProject.global.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginReqDto {

	@Email
	@NotBlank(message = "이메일은 필수입니다.")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;
}
