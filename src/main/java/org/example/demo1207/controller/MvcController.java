package org.example.demo1207.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.example.demo1207.context.UserContext;
import org.example.demo1207.model.User;
import org.example.demo1207.service.UserService;

import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Setter
@Controller
@RequiredArgsConstructor
public class MvcController {

    private final UserService userService;


    @GetMapping("user-home")
    String getIndexPage(@RequestParam(required = false, defaultValue = "", name = "first-name") String firstName,
                        @RequestParam(required = false, defaultValue = "", name = "last-name") String lastName,
                        @RequestParam(required = false, defaultValue = "") String email,
                        Pageable pageable, Model model, HttpServletRequest request) {

        Integer errorStatus = (Integer) request.getAttribute("errorStatus");
        String errorMessage = (String) request.getAttribute("errorMessage");

        if (errorStatus != null && errorStatus != 500) {
            model.addAttribute("status", errorStatus);
            model.addAttribute("error", errorMessage);

            return "error";
        }

        User user = UserContext.getCurrentUser();
        if (user != null) {
            firstName = user.getFirstname();
            lastName = user.getLastname();
            email = user.getEmail();
        }

        var users = userService.getUsersPage(firstName, lastName, email, pageable, request);

        model.addAttribute("users", users.get("content"));
        model.addAttribute("firstName", firstName);
        model.addAttribute("lastName", lastName);
        model.addAttribute("email", email);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("size", pageable.getPageSize());
        model.addAttribute("totalPages", users.get("totalPages"));

        return "index";
    }
}
