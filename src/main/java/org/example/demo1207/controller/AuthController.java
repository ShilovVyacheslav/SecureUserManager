package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;

import org.example.demo1207.model.Role;
import org.example.demo1207.model.User;

import org.example.demo1207.request.AuthenticationRequest;
import org.example.demo1207.request.RegisterRequest;
import org.example.demo1207.response.AuthenticationResponse;

import org.example.demo1207.service.JwtService;
import org.example.demo1207.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) throws IllegalAccessException {
        if (!request.allFieldsAreFilled()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            new AuthenticationResponse(
                                    "Not all fields are filled in correctly."
                            )
                    );
        }
        Optional<User> userData = userService.createUser(
                User.builder()
                        .id(UUID.randomUUID().toString())
                        .firstname(request.getFirstname())
                        .lastname(request.getLastname())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .roles(Set.of(Role.ROLE_USER))
                        .build()
        );

        if (userData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(
                            new AuthenticationResponse(
                                    "User with email " + request.getEmail() + " already exists."
                            )
                    );
        }
        var jwtToken = jwtService.generateToken(userData.orElse(null));
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            var jwtToken = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse("Invalid email or password."));
        }
    }
}
