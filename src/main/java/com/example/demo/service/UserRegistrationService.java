package com.example.demo.service;

import com.example.demo.payload.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserRegistrationService {
    UserDto registerUser(UserDto userDto) throws IOException;
    String uploadUserPhoto(MultipartFile photo) throws IOException;
    List<UserDto> getAllUsers();
}

