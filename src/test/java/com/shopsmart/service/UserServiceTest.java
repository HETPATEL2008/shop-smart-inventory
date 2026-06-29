package com.shopsmart.service;

import com.shopsmart.dao.UserDAO;
import com.shopsmart.dao.UserDAOImpl;

import com.shopsmart.dto.UserRegisterDTO;
import com.shopsmart.dto.UserResponseDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOImpl();
        userService = new UserService(userDAO);
    }

    // Helper method to generate unique test email
    private String generateUniqueEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@shopsmart.com";
    }


    // 1. Tests for registerUser method

    @Test
    void registerUser_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        String email = generateUniqueEmail();

        UserRegisterDTO setUpUser = new UserRegisterDTO("First User", email, "SecureP@ss135");
        userService.registerUser(setUpUser);

        UserRegisterDTO duplicateUser = new UserRegisterDTO("Second User", email, "DuplicateP@ss246");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(duplicateUser));
        assertEquals("An account with this email already exists.", exception.getMessage());
    }

    @Test
    void registerUser_PasswordTooShort_ThrowsIllegalArgumentException() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("Raj Sharma", generateUniqueEmail(), "P@s12");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userRegisterDTO));
        assertEquals("Password must be at least 6 characters long.", exception.getMessage());
    }

    @Test
    void registerUser_PasswordFailsComplexityCriteria_ThrowsIllegalArgumentException() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("Raj Sharma", generateUniqueEmail(), "nocaps123!");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(userRegisterDTO));
        assertTrue(exception.getMessage().contains("Password must contain at least one uppercase letter"));
    }

    @Test
    void registerUser_PasswordContainsRepetitiveCharacters_ThrowsIllegalArgumentException() {
        UserRegisterDTO dto = new UserRegisterDTO("John Doe", generateUniqueEmail(), "ValidP@ssaaa1"); // Contains repetitive 'aaa'

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(dto));
        assertEquals("Password cannot contain 3 or more repetitive identical characters (e.g., 'aaa').", exception.getMessage());
    }

    @Test
    void registerUser_PasswordContainsForwardSequence_ThrowsIllegalArgumentException() {
        UserRegisterDTO registerDTO = new UserRegisterDTO("Raj Sharma", generateUniqueEmail(), "ValidP@ss123!"); // Contains forward sequential '123'

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDTO));
        assertEquals("Password cannot contain alphabetical or numerical sequences (e.g., 'abc', '123').", exception.getMessage());
    }

    @Test
    void registerUser_PasswordContainsBackwardSequence_ThrowsIllegalArgumentException() {
        UserRegisterDTO registerDTO = new UserRegisterDTO("Raj Sharma", generateUniqueEmail(), "ValidP@ss321!"); // Contains backward sequential '321'

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.registerUser(registerDTO));
        assertEquals("Password cannot contain reverse alphabetical or numerical sequences (e.g., 'cba', '321').", exception.getMessage());
    }

    @Test
    void registerUser_Success_ReturnsMappedUserResponseDTO() {
        String email = generateUniqueEmail();
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO("Raj Sharma", email, "ValidP@ss135!");

        Optional<UserResponseDTO> responseDTO = userService.registerUser(userRegisterDTO);

        assertTrue(responseDTO.isPresent());
        assertTrue(responseDTO.get().getId() > 0);
        assertEquals("Raj Sharma", responseDTO.get().getName());
        assertEquals(email, responseDTO.get().getEmail());
        assertNotNull(responseDTO.get().getCreatedAt());
    }


    // 2. Tests for loginUser method

    @Test
    void loginUser_NullInputs_ThrowsIllegalArgumentException() {
        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(null, "SecureP@ss135"));
        assertEquals("Email and password inputs cannot be empty.", ex1.getMessage());

        // Test Null Password
        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> userService.loginUser(generateUniqueEmail(), null));
        assertEquals("Email and password inputs cannot be empty.", ex2.getMessage());
    }

    @Test
    void loginUser_EmailNotFound_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.loginUser("non_existent_profile_id_email_string@shopsmart.com", "ValidP@ss135!")
        );
        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    void loginUser_IncorrectPassword_ThrowsIllegalArgumentException() {
        String uniqueEmail = generateUniqueEmail();
        UserRegisterDTO setupDto = new UserRegisterDTO("Raj Sharma", uniqueEmail, "CorrectP@ss135!");
        userService.registerUser(setupDto);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.loginUser(uniqueEmail, "WrongPassword1!")
        );
        assertEquals("Invalid email or password.", exception.getMessage());
    }

    @Test
    void loginUser_Success_ReturnsUserResponseDTO() {
        String uniqueEmail = generateUniqueEmail();
        UserRegisterDTO setupDto = new UserRegisterDTO("Raj Sharma", uniqueEmail, "SecretP@ss135");
        userService.registerUser(setupDto);

        Optional<UserResponseDTO> response = userService.loginUser(uniqueEmail, "SecretP@ss135");

        assertTrue(response.isPresent());
        assertTrue(response.get().getId() > 0);
        assertEquals("Raj Sharma", response.get().getName());
        assertEquals(uniqueEmail, response.get().getEmail());
    }


    // 3. Test for getUSerProfileById method

    @Test
    void getUserProfileById_UserDoesNotExist_ThrowsRuntimeException() {
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserProfileById(-999));
        assertEquals("The requested user profile could not be found.", exception.getMessage());
    }

    @Test
    void getUserProfileById_Success_ReturnsUserResponseDTO() {
        String uniqueEmail = generateUniqueEmail();
        UserRegisterDTO setupDto = new UserRegisterDTO("Raj Sharma", uniqueEmail, "ValidP@ss153!");
        UserResponseDTO savedUser = userService.registerUser(setupDto).orElseThrow();

        // Extract using the exact runtime ID generated by your physical relational database row
        Optional<UserResponseDTO> response = userService.getUserProfileById(savedUser.getId());

        assertTrue(response.isPresent());
        assertEquals(savedUser.getId(), response.get().getId());
        assertEquals("Raj Sharma", response.get().getName());
        assertEquals(uniqueEmail, response.get().getEmail());
    }
}
