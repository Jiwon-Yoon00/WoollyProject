package com.example.WoollyProject.domain.user.entity;

import org.hibernate.annotations.ColumnDefault;

import com.example.WoollyProject.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	private String email;

	private String password;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'USER'")
	@Column(nullable = false, length = 10)
	private Role role;
}
