package com.example.WoollyProject.domain.auth.entity;

public enum BlacklistReason {
	LOGOUT,            // 사용자가 직접 로그아웃
	ADMIN_FORCED,      // 관리자가 강제 로그아웃
	SUSPICIOUS_LOGIN   // 다른 IP / 위치에서 로그인 탐지
}
