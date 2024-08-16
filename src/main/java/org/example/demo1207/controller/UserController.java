package org.example.demo1207.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.example.demo1207.dto.UserDto;
import org.example.demo1207.model.User;

import org.example.demo1207.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) throws IllegalAccessException {
        Optional<User> userData = userService.createUser(user);
        return userData.map(createdUser -> new ResponseEntity<>(createdUser.mapUserToDto(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("view/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readAllUsers(HttpServletRequest request) {
        List<UserDto> userList = userService.readAllUsers(request);
        if (userList == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (userList.isEmpty()) return new ResponseEntity<>(userList, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("view/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readUserById(@PathVariable String id, HttpServletRequest request) {
        Optional<UserDto> userData = userService.readUserById(id, request);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping({"view", "view/me"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> readYourself(HttpServletRequest request) {
        Optional<UserDto> userData = userService.readUserByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                request
        );
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("change/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserById(@PathVariable String id,
                                            @RequestBody User newUserData) throws IllegalAccessException {
        Optional<UserDto> userData = userService.updateUserById(id, newUserData);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping({"change", "change/me"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateYourself(@RequestBody User newUserData) throws IllegalAccessException {
        Optional<UserDto> userData = userService.updateUserByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName(), newUserData
        );
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable String id) throws IllegalAccessException {
        return userService.deleteUserById(id) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("view/search-user")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> searchUsers(@RequestParam(required = false, defaultValue = "", name = "first-name") String firstName,
                                  @RequestParam(required = false, defaultValue = "", name = "last-name") String lastName,
                                  @RequestParam(required = false, defaultValue = "") String email,
                                  Pageable pageable, HttpServletRequest request) {
        return ResponseEntity.ok(userService.getUsersPage(firstName, lastName, email, pageable, request));
    }
}
