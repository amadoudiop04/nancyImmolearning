package com.nancyimmo.bailleur.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String hello() {
        return "Api is working";
    }
}
