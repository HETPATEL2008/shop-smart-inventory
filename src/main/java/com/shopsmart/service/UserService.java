package com.shopsmart.service;

import com.shopsmart.dao.UserDAO;

import com.shopsmart.dto.UserRegisterDTO;
import com.shopsmart.dto.UserResponseDTO;

import com.shopsmart.model.User;

import org.mindrot.jbcrypt.BCrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Method for register new user
    public Optional<UserResponseDTO> registerUser(UserRegisterDTO registerDTO) {
        logger.info("Registration attempt initiated for email: {}", registerDTO.getEmail());

        Optional<User> existingUser = userDAO.getUserByEmail(registerDTO.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Registration failed: Email '{}' is already taken.", registerDTO.getEmail());
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        String password = registerDTO.getPassword();

        // Password should be not null and must be minimum 6 characters long
        if (password == null || password.length() < 6) {
            logger.warn("Registration failed for '{}': Password is too short.", registerDTO.getEmail());
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }

        // At least 1 uppercase, 1 lowercase, 1 digit, 1 special char, and no whitespaces
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{}|;':\",./<>?~`\\s])\\S+$";
        if (!password.matches(passwordRegex)) {
            logger.warn("Registration failed for '{}': Password fails complexity criteria.", registerDTO.getEmail());
            throw new IllegalArgumentException("Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and no whitespaces.");
        }

        // Scan for Repetitive and Sequential Characters
        int repeatCount = 1;
        int forwardCount = 1;
        int backwardCount = 1;

        for (int i = 1; i < password.length(); i++) {
            char current = password.charAt(i);
            char previous = password.charAt(i - 1);

            // Track Repetitive (e.g., "aaa", "111")
            if (current == previous) {
                repeatCount++;
                if (repeatCount >= 3) {
                    logger.warn("Registration failed for '{}': Password contains repetitive characters.", registerDTO.getEmail());
                    throw new IllegalArgumentException("Password cannot contain 3 or more repetitive identical characters (e.g., 'aaa').");
                }
            } else {
                repeatCount = 1;
            }

            // Track Sequential Forward (e.g., "abc", "123")
            if (current == previous + 1) {
                forwardCount++;
                if (forwardCount >= 3) {
                    logger.warn("Registration failed for '{}': Password contains forward sequential characters.", registerDTO.getEmail());
                    throw new IllegalArgumentException("Password cannot contain alphabetical or numerical sequences (e.g., 'abc', '123').");
                }
            } else {
                forwardCount = 1;
            }

            // Track Sequential Backward (e.g., "cba", "321")
            if (current == previous - 1) {
                backwardCount++;
                if (backwardCount >= 3) {
                    logger.warn("Registration failed for '{}': Password contains backward sequential characters.", registerDTO.getEmail());
                    throw new IllegalArgumentException("Password cannot contain reverse alphabetical or numerical sequences (e.g., 'cba', '321').");
                }
            } else {
                backwardCount = 1;
            }
        }

        try {
            String securePasswordHash = BCrypt.hashpw(password, BCrypt.gensalt());

            User user = new User();
            user.setName(registerDTO.getName());
            user.setEmail(registerDTO.getEmail());
            user.setPasswordHash(securePasswordHash);

            boolean isSaved = userDAO.registerUser(user);

            if (!isSaved) {
                logger.error("Database layer rejected the user registration insert operation.");
                throw new RuntimeException("Registration failed due to a database insertion error.");
            }

            logger.info("User registered successfully in DB.");

            return userDAO.getUserByEmail(registerDTO.getEmail())
                    .map(savedUser -> new UserResponseDTO(
                            savedUser.getId(),
                            savedUser.getName(),
                            savedUser.getEmail(),
                            savedUser.getCreatedAt()
                    ));

        } catch (Exception e) {
            logger.error("Critical database infrastructure error occurred during user creation", e);
            throw new RuntimeException("Registration failed due to internal database connectivity issues.");
        }
    }

    // Method for authenticate existing user
    public Optional<UserResponseDTO> loginUser(String email, String rawPassword) {
        logger.info("Login process initiated for user identifier: {}", email);

        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("Email and password inputs cannot be empty.");
        }

        return Optional.of(userDAO.getUserByEmail(email)
                .filter(user -> {
                    boolean matches = BCrypt.checkpw(rawPassword, user.getPasswordHash());
                    if (!matches) {
                        logger.warn("Security Event: Authentication failed for user '{}' due to invalid credentials.", email);
                    }
                    return matches;
                })
                .map(user -> {
                    logger.info("User '{}' successfully authenticated and logged in.", email);
                    return new UserResponseDTO(
                            user.getId(), user.getName(), user.getEmail(), user.getCreatedAt()
                    );
                })
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password.")));
    }

    // Method for find user by id
    public Optional<UserResponseDTO> getUserProfileById(int userId) {
        return Optional.of(userDAO.getUserById(userId)
                .map(user -> {
                    logger.info("Successfully fetched details for User ID {}. Mapping safe output data payload.", userId);
                    return new UserResponseDTO(
                            user.getId(), user.getName(), user.getEmail(), user.getCreatedAt()
                    );
                })
                .orElseThrow(() -> {
                    logger.warn("Profile lookup failed: User ID {} does not exist in database records.", userId);
                    return new RuntimeException("The requested user profile could not be found.");
                }));
    }
}
