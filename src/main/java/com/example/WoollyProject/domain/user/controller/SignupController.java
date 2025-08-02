package com.example.WoollyProject.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.WoollyProject.domain.user.dto.request.SignupReqDto;
import com.example.WoollyProject.domain.user.dto.response.SignupResDto;
import com.example.WoollyProject.domain.user.service.SignupService;
import com.example.WoollyProject.global.dto.ApiRes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SignupController {

	private final SignupService signupService;

	@PostMapping("/users/signup")
	public ResponseEntity<ApiRes<SignupResDto>> signup(@Valid @RequestBody SignupReqDto signupReqDto) {
		return ResponseEntity.ok(ApiRes.ok(signupService.signup(signupReqDto)));
	}
}
