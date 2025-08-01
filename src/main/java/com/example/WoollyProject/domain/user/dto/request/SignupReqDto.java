package com.example.WoollyProject.domain.user.dto.request;

import com.example.WoollyProject.domain.user.entity.Role;
import com.example.WoollyProject.domain.user.entity.User;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupReqDto {

	@NotBlank(message = "닉네임은 필수입니다.")
	private String nickname;

	@NotBlank(message = "이메일은 필수입니다.")
	@Email
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인은 필수입니다.")
	private String confirmPassword;

	@AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
	public boolean isPasswordMatching() {
		return password != null && password.equals(confirmPassword);
	}

	public User toEntity(String encodedPassword){
		return User.builder()
			.nickname(nickname)
			.email(email)
			.password(encodedPassword)
			.role(Role.USER)
			.build();
	}
}
