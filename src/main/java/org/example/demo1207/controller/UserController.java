package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.demo1207.model.User;
import org.example.demo1207.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Optional<User> userData = userService.createUser(user);
        return userData.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<User>> readAllUsers() {
        List<User> userList = userService.readAllUsers();
        if (userList == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (userList.isEmpty()) {
            return new ResponseEntity<>(userList, HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> readUserById(@PathVariable String id) {
        Optional<User> userData = userService.readUserById(id);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUserById(@PathVariable String id, @RequestBody User newUserData) {
        Optional<User> userData = userService.updateUserById(id, newUserData);
        return userData.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.OK));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<HttpStatus> deleteUserById(@PathVariable String id) {
        return userService.deleteUserById(id) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("search-user")
    ResponseEntity<?> searchUsers(@RequestParam(required=false, defaultValue="", name="first-name") String firstName,
                                  @RequestParam(required=false, defaultValue="", name="last-name") String lastName,
                                  @RequestParam(required=false, defaultValue="") String email,
                                  Pageable pageable) {
        firstName = StringUtils.trimToNull(firstName);
        lastName = StringUtils.trimToNull(lastName);
        email = StringUtils.trimToNull(email);
        return ResponseEntity.ok(userService.getUsersPage(firstName, lastName, email, pageable));
    }

}
