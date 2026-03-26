package com.kishore.ticketbooking.controller;

import com.kishore.ticketbooking.config.JwtUtil;
import com.kishore.ticketbooking.entity.User;
import com.kishore.ticketbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allows your React app to talk to this desk
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // =========================================
    // 1. REGISTRATION ENDPOINT
    // =========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Step 1: Check if the email is already in the database
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email is already taken!");
        }

        // Step 2: Create a new user
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());
        
        // 🔥 CRITICAL: Scramble the password before saving it to MySQL!
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); 

        // Step 3: Save to database
        userRepository.save(newUser);
        return ResponseEntity.ok("User registered successfully!");
    }

    // =========================================
    // 2. LOGIN ENDPOINT
    // =========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Step 1: Look up the user by email
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Step 2: Check if the typed password matches the scrambled database password
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                
                // Step 3: Passwords match! Print the VIP Wristband (JWT)
                String token = jwtUtil.generateToken(user.getEmail());
                
                // Hand the token back to React
                return ResponseEntity.ok(new AuthResponse(token));
            }
        }
        // If email doesn't exist OR password is wrong, kick them out.
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password!");
    }

    // =========================================
    // DATA TRANSFER OBJECTS (DTOs)
    // These mini-classes catch the JSON data sent by React
    // =========================================
    public static class RegisterRequest {
        private String name;
        private String email;
        private String password;
        
        public String getName() { return name; } 
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; } 
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; } 
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; } 
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; } 
        public void setPassword(String password) { this.password = password; }
    }

    public static class AuthResponse {
        private String token;
        public AuthResponse(String token) { this.token = token; }
        public String getToken() { return token; } 
        public void setToken(String token) { this.token = token; }
    }
}