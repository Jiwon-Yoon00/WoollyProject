package com.example.WoollyProject.domain.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenResDto {

	@NotBlank(message = "토큰은 필수입니다.")
	private String accessToken;

	@NotBlank(message = "토큰은 필수입니다.")
	private String refreshToken;
}
