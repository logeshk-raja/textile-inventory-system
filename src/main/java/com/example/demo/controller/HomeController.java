package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // ROOT URL
    @GetMapping("/")
    public String home() {
        return "index";   // index.html
    }

    // /index URL
    @GetMapping("/index")
    public String index() {
        return "index";   // index.html
    }
}