package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final UserController userController;
    private final ChangeController changeController;

    @GetMapping("changes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> readAllChanges() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication());
        return changeController.readAllChanges();
    }

    @GetMapping("users")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> readAllUsers() {
        return userController.readAllUsers();
    }

}
