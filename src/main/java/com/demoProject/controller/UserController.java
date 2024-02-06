package com.demoProject.controller;

import com.demoProject.dto.RequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@EnableMethodSecurity(prePostEnabled = false)
public class UserController {
    @GetMapping("/home")
    ResponseEntity<RequestDto> welcome(){
        return ResponseEntity.ok().body(RequestDto.builder().response("Welcome to eProcess").build());
    }
}
