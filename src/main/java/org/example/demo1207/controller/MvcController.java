package org.example.demo1207.controller;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
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
    String getIndexPage(@RequestParam(required=false, defaultValue="", name="first-name") String firstName,
                        @RequestParam(required=false, defaultValue="", name="last-name") String lastName,
                        @RequestParam(required=false, defaultValue="") String email,
                        Pageable pageable, Model model) {

        firstName = StringUtils.trimToNull(firstName);
        lastName = StringUtils.trimToNull(lastName);
        email = StringUtils.trimToNull(email);

        var users = userService.getUsersPage(firstName, lastName, email, pageable);

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
