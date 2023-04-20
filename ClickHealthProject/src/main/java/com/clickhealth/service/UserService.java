package com.clickhealth.service;

import com.clickhealth.dto.UserDto;
import com.clickhealth.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
