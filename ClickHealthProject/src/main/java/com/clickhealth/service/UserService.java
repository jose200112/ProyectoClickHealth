package com.clickhealth.service;


import java.util.List;

import com.clickhealth.dto.UserDto;
import com.clickhealth.entity.User;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
