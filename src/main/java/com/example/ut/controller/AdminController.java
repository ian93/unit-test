package com.example.ut.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/admin")
@Controller
public class AdminController {

    @GetMapping(value = "/welcome")
    public String welcome() {
        return "admin/welcome";
    }
}
