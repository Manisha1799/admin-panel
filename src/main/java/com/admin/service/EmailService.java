package com.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        try {
            log.debug("Attempting to send OTP email to: {}", to);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP for Admin Panel");
            message.setText("Your verification code is: " + otp + "\n\n" +
                    "Please enter this code to verify your email address.");
            mailSender.send(message);
            log.debug("Successfully sent OTP email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
} 