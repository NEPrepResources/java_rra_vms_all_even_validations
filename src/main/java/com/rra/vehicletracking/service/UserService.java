package com.rra.vehicletracking.service;

import com.rra.vehicletracking.config.JwtUtil;
import com.rra.vehicletracking.dto.UserDTO;
import com.rra.vehicletracking.entity.Role;
import com.rra.vehicletracking.entity.User;
import com.rra.vehicletracking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User signup(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String roleStr = userDTO.getRole().toUpperCase();
        if (!roleStr.startsWith("ROLE_")) {
            roleStr = "ROLE_" + roleStr;
        }

        Role requestedRole;
        try {
            requestedRole = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role specified. Valid roles are: ROLE_ADMIN, ROLE_STANDARD");
        }

        if (requestedRole == Role.ROLE_ADMIN) {
            boolean adminExists = userRepository.findByRole(Role.ROLE_ADMIN).isPresent();
            if (adminExists) {
                throw new RuntimeException("Admin user already exists. Signup as admin is not allowed.");
            } else {
                throw new RuntimeException("Signup as admin is not allowed.");
            }
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setNationalId(userDTO.getNationalId());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(requestedRole);

        user = userRepository.save(user);
        logger.info("User registered: {}", user.getEmail());

        return user;
    }

    public com.rra.vehicletracking.dto.AuthResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getRole().name());

        return com.rra.vehicletracking.dto.AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .address(user.getAddress())
                .phone(user.getPhone())
                .nationalId(user.getNationalId())
                .build();
    }

    public com.rra.vehicletracking.dto.AuthResponseDTO refreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is required");
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String accessToken = jwtUtil.generateAccessTokenFromRefreshToken(refreshToken);

            return com.rra.vehicletracking.dto.AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken) // Return the same refresh token
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .address(user.getAddress())
                    .phone(user.getPhone())
                    .nationalId(user.getNationalId())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    public void initAdmin() {
        String adminEmail = "admin@rra.rw";
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            throw new RuntimeException("Admin already exists");
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPhone("+250700000000");
        admin.setNationalId("1199999999999999");
        admin.setPassword(passwordEncoder.encode("admin@123RA"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setAddress("Kigali, Rwanda");

        userRepository.save(admin);
        logger.info("Admin user created: {}", adminEmail);
    }

    public Page<UserDTO> listUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setName(user.getName());
                    dto.setAddress(user.getAddress());
                    dto.setEmail(user.getEmail());
                    dto.setPhone(user.getPhone());
                    dto.setNationalId(user.getNationalId());
                    dto.setRole(user.getRole().name());
                    return dto;
                });
    }
}
