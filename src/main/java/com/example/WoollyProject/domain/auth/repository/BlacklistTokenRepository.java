package com.example.WoollyProject.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.WoollyProject.domain.auth.entity.BlacklistToken;

public interface BlacklistTokenRepository extends CrudRepository<BlacklistToken, String> {
	boolean existsById(String token); // 블랙리스트 여부 체크
}
