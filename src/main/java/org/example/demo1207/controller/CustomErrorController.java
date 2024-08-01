package org.example.demo1207.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(@RequestParam(required=false, defaultValue="", name="status") Integer statusCode,
                              @RequestParam(required=false, defaultValue="", name="error") String errorMessage,
                              Model model) {

        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorMessage);

        return "error";
    }

    public String getErrorPath() {
        return "/error";
    }
}