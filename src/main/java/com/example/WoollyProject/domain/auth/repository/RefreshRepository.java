package com.example.WoollyProject.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.WoollyProject.domain.auth.entity.RefreshEntity;

import jakarta.transaction.Transactional;

public interface RefreshRepository extends JpaRepository<RefreshEntity, Long> {

	Boolean existsByRefresh(String refresh);

	@Transactional
	void deleteByRefresh(String refresh);
}
