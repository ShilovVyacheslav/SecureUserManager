package org.example.demo1207.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.demo1207.dto.UserDto;
import org.example.demo1207.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    Optional<User> createUser(User user) throws IllegalAccessException;

    List<UserDto> readAllUsers(HttpServletRequest request);

    Optional<UserDto> readUserById(String id, HttpServletRequest request);

    Optional<UserDto> readUserByEmail(String email, HttpServletRequest request);

    Optional<UserDto> updateUserById(String id, User newUser) throws IllegalAccessException;

    Optional<UserDto> updateUserByEmail(String email, User newUser) throws IllegalAccessException;

    boolean deleteUserById(String id) throws IllegalAccessException;

    Map<String, Object> getUsersPage(String firstName, String lastName, String email,
                                     Pageable pageable, HttpServletRequest request);

    void saveView(HttpServletRequest request);

    void saveChange(User oldUser, User newUser) throws IllegalAccessException;
}
