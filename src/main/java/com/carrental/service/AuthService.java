package com.carrental.service;

import com.carrental.dto.LoginRequest;
import com.carrental.dto.LoginResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.entity.UserEntity;
import com.carrental.entity.UserRole;
import com.carrental.repository.UserRepository;
import com.carrental.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    // ✅ Implementing the login method
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token, "User logged in successfully!");
    }

    // ✅ Properly implementing the register method
    public LoginResponse register(RegisterRequest request) {
        // Check if the username or email is already taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken.");
        }

        // Create new user
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        
        // Set role based on email domain (for demo purposes)
        if (request.getEmail().endsWith("@admin.com")) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token, "User registered successfully!");
    }

    public LoginResponse registerAdmin(RegisterRequest request) {
        if (!request.getEmail().endsWith("@admin.com")) {
            throw new RuntimeException("Invalid admin email domain");
        }

        // Check if the username or email is already taken
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken.");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already taken.");
        }

        // Create new admin user
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(UserRole.ADMIN);

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return new LoginResponse(token, "Admin user registered successfully!");
    }
}
