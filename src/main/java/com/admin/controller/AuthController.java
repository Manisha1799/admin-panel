package com.admin.controller;

import com.admin.dto.AuthenticationRequest;
import com.admin.dto.AuthenticationResponse;
import com.admin.dto.OtpVerificationRequest;
import com.admin.dto.RegisterRequest;
import com.admin.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationService authService;


    @PostMapping("/register")
    public AuthenticationResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }


    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest request) {
        return authService.login(request);
    }


    @PostMapping("/verify")
    public AuthenticationResponse verifyEmail(@Valid @RequestBody OtpVerificationRequest request) {
        return authService.verify(request.getEmail(), request.getOtp());
    }
}