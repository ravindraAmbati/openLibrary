package com.read.data.openLibrary.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthCheckController {

    @GetMapping
    public String init(){
        return "App is up and running!";
    }

    @GetMapping("/test")
    public String test(){
        return "Hello World!";
    }
}
