package com.example.WoollyProject.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.WoollyProject.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	boolean existsByEmail(String email);
}
