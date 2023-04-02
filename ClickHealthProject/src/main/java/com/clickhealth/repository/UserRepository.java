package com.clickhealth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clickhealth.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
