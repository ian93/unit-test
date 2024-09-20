package com.example.ut.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.ut.service.LoginService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class LoginController {

    private final LoginService loginService;

    @GetMapping(value = "/login")
    public String getLoginPage() {
        return "login";
    }

    @PostMapping(value = "/authenticate")
    public String login(@RequestParam String username, @RequestParam String password) {
        boolean login = loginService.login(username, password);
        if (!login) {
            return "redirect:/login?error";
        }

        return "redirect:/index";
    }
}
