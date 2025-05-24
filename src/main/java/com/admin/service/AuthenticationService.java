package com.admin.service;

import com.admin.dto.AuthenticationRequest;
import com.admin.dto.AuthenticationResponse;
import com.admin.dto.RegisterRequest;
import com.admin.exception.CountryRestrictedException;
import com.admin.model.User;
import com.admin.repository.UserRepository;
import com.admin.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;
    private final HttpServletRequest request;

    public AuthenticationResponse register(RegisterRequest request) {
        try {
            // Get client IP address
            String clientIp = getClientIp();

            // Check country restriction
            String country = locationService.getCountryFromIp(clientIp);
            if (locationService.isBlockedCountry(country)) {
                throw new CountryRestrictedException("Registration not allowed from " + country);
            }

            if (userRepository.existsByEmail(request.getEmail())) {
                return new AuthenticationResponse(false, "Email already registered");
            }
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return new AuthenticationResponse(false, "Passwords do not match");
            }

            String password = request.getPassword();
            if (!isValidPassword(password)) {
                return new AuthenticationResponse(false,
                        "Password must be at least 8 characters long and contain at least one number, one uppercase letter, and one special character");
            }
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setVerificationCode(generateVerificationCode());
            userRepository.save(user);

            emailService.sendOtpEmail(user.getEmail(), user.getVerificationCode());

            return new AuthenticationResponse(true, "Registration successful. Please check your email for verification code.");
        } catch (CountryRestrictedException e) {
            return new AuthenticationResponse(false, e.getMessage());
        } catch (Exception e) {
            return new AuthenticationResponse(false, "Registration failed: " + e.getMessage());
        }
    }

    public AuthenticationResponse verify(String email, String otp) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getVerified()) {
                return new AuthenticationResponse(false, "Email already verified");
            }

            if (!otp.equals(user.getVerificationCode())) {
                return new AuthenticationResponse(false, "Invalid OTP code");
            }

            user.setVerified(true);
            user.setVerificationCode(null);
            userRepository.save(user);

            String token = jwtService.generateToken(user.getEmail());
            return new AuthenticationResponse(true, "Email verified successfully", token);
        } catch (Exception e) {
            return new AuthenticationResponse(false, "Verification failed: " + e.getMessage());
        }
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getVerified()) {
                return new AuthenticationResponse(false, "Please verify your email first");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthenticationResponse(false, "Invalid password");
            }

            String token = jwtService.generateToken(user.getEmail());
            return new AuthenticationResponse(true, "Login successful", token);
        } catch (Exception e) {
            return new AuthenticationResponse(false, "Login failed: " + e.getMessage());
        }
    }

    private String generateVerificationCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        return hasNumber && hasUpperCase && hasSpecialChar;
    }

    private String getClientIp() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
