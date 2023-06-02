package com.clickhealth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
