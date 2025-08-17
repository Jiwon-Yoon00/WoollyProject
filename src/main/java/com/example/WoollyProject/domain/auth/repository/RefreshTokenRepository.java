package com.example.WoollyProject.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.example.WoollyProject.domain.auth.entity.RefreshEntity;
import com.example.WoollyProject.domain.auth.entity.RefreshTokenRedis;

//redis용
public interface RefreshTokenRepository extends CrudRepository<RefreshTokenRedis, String> {

}
