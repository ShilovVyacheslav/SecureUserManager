package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;

import org.example.demo1207.dto.UserDto;
import org.example.demo1207.model.User;

import org.example.demo1207.service.ChangeService;
import org.example.demo1207.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ChangeService changeService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody User user) {
        Optional<User> userData = userService.createUser(user);
        return userData.map(createdUser -> new ResponseEntity<>(createdUser.mapUserToDto(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<?> readAllUsers() {
        List<UserDto> userList = userService.readAllUsers();
        if (userList == null) return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        if (userList.isEmpty()) return new ResponseEntity<>(userList, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> readUserById(@PathVariable String id) {
        Optional<User> userData = userService.readUserById(id);
        return userData.map(user -> new ResponseEntity<>(user.mapUserToDto(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateUserById(@PathVariable String id,
                                            @RequestBody User newUserData) throws IllegalAccessException {
        Optional<User> oldUserData = userService.readUserById(id);
        if (oldUserData.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Optional<User> userData = userService.updateUserById(id, newUserData);
        if (userData.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return userService.saveLog(changeService, oldUserData.get(), userData.get()) ?
                new ResponseEntity<>(userData.get().mapUserToDto(), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable String id) {
        return userService.deleteUserById(id) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("search-user")
    ResponseEntity<?> searchUsers(@RequestParam(required = false, defaultValue = "", name = "first-name") String firstName,
                                  @RequestParam(required = false, defaultValue = "", name = "last-name") String lastName,
                                  @RequestParam(required = false, defaultValue = "") String email,
                                  Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersPage(firstName, lastName, email, pageable));
    }

}
