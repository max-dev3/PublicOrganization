package com.example.backend.service.user;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.backend.exception.InvalidInputException;
import com.example.backend.exception.InvalidPasswordException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.EntityBuilder;
import com.example.backend.service.UserService;

public class UserServiceTest extends EntityBuilder {
    
    @Mock private UserRepository userRepository;
    @Spy private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void getAllUsersTest() {
        var userList = List.of( getUserEntity(), getUserEntity() );
        when( userRepository.findAll() ).thenReturn( userList );

        var result = userService.getAllUsers();

        verify( userRepository, times(1) ).findAll();
        assertEquals(2, result.size());
        assertListEquals(userList, result);
    }

    @Test
    void getUserByIdTest() {
        var user = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));

        var result = assertDoesNotThrow( () -> userService.getUserById(user.getId()) );

        verify( userRepository, times(1) ).findById(user.getId());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void getUserByIdInvalidInputExceptionTest() {
        var user = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(false);

        var result = assertThrows(InvalidInputException.class, () -> userService.getUserById(user.getId()));

        verify( userRepository, times(0) ).findById(user.getId());
        assertEquals("User with id " + user.getId() + " not found.", result.getMessage());
    }

    @Test
    void createUserTest() {
        var user = getUserEntity();
        when( userRepository.save(user) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = assertDoesNotThrow( () -> userService.createUser(user) );

        verify( userRepository, times(1) ).save(user);
        assertEquals(user.getId(), result.getId());
        assertEquals(Role.USER, result.getRole());
        String expectedEncodedPassword = passwordEncoder.encode(user.getPassword());
        assertTrue(passwordEncoder.matches(result.getPassword(), expectedEncodedPassword) );
    }

    @Test
    void createUser_UserAlreadyExistsTest() {
        var user = getUserEntity();
        when( userRepository.findByUsername(anyString()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Username " + user.getUsername() + " already exists", result.getMessage());
    }

    @Test
    void createUser_UserEmailAlreadyExistsTest() {
        var user = getUserEntity();
        when( userRepository.findByEmail(anyString()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Email " + user.getEmail() + " already exists", result.getMessage());
    }

    @Test
    void createUser_UserPhoneAlreadyExistsTest() {
        var user = getUserEntity();
        when( userRepository.findByPhoneNumber(anyString()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Phone number " + user.getPhoneNumber() + " already exists", result.getMessage());
    }

    @Test
    void createUser_UsernameTooShortTest() {
        var user = getUserEntity();
        user.setUsername("short");
        when( userRepository.save(user) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Username must be at least 6 characters long", result.getMessage());
    }

    @Test
    void createUser_InvalidEmailTest() {
        var user = getUserEntity();
        user.setEmail("invalid");
        when( userRepository.save(user) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Invalid email", result.getMessage());
    }

    @Test
    void createUser_InvalidPhoneNumberTest() {
        var user = getUserEntity();
        user.setPhoneNumber("invalid");
        when( userRepository.save(user) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = assertThrows(InvalidInputException.class, () -> userService.createUser(user));

        verify( userRepository, times(0) ).save(user);
        assertEquals("Invalid phone number format: " + user.getPhoneNumber(), result.getMessage());
    }

    @Test
    void deleteUserTest() {
        var user = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(true);

        assertDoesNotThrow( () -> userService.deleteUser(user.getId()) );

        verify( userRepository, times(1) ).deleteById(user.getId());
    }

    @Test
    void deleteUserInvalidInputExceptionTest() {
        var user = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(false);

        var result = assertThrows(InvalidInputException.class, () -> userService.deleteUser(user.getId()));

        verify( userRepository, times(0) ).deleteById(user.getId());
        assertEquals("User with id " + user.getId() + " not found.", result.getMessage());
    }

    @Test
    void updateUserTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        newUser.setRole(Role.ADMIN);
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.save(any(User.class)) ).thenAnswer( invocation -> invocation.getArgument(0) );
        
        var result = assertDoesNotThrow(() -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(1) ).save(any(User.class));
        assertEquals(newUser.getFirstName(), result.getFirstName());
        assertEquals(newUser.getLastName(), result.getLastName());
        assertEquals(newUser.getUsername(), result.getUsername());
        assertEquals(newUser.getEmail(), result.getEmail());
        assertEquals(newUser.getPhoneNumber(), result.getPhoneNumber());
        assertEquals(newUser.getRole(), result.getRole());
    }

    @Test
    void updateUserInvalidInputExceptionTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(false);

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("User with id " + user.getId() + " not found.", result.getMessage());
    }

    @Test
    void updateUser_UsernameAlreadyExistsTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.findByUsername(anyString()) ).thenReturn(java.util.Optional.of(newUser));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Username " + newUser.getUsername() + " already exists", result.getMessage());
    }

    @Test
    void updateUser_EmailAlreadyExistsTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.findByEmail(anyString()) ).thenReturn(java.util.Optional.of(newUser));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Email " + newUser.getEmail() + " already exists", result.getMessage());
    }

    @Test
    void updateUser_PhoneAlreadyExistsTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.findByPhoneNumber(anyString()) ).thenReturn(java.util.Optional.of(newUser));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Phone number " + newUser.getPhoneNumber() + " already exists", result.getMessage());
    }

    @Test
    void updateUser_InvalidEmailTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        newUser.setEmail("invalid");
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Invalid email", result.getMessage());
    }

    @Test
    void updateUser_InvalidPhoneNumberTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        newUser.setPhoneNumber("invalid");
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Invalid phone number format: " + newUser.getPhoneNumber(), result.getMessage());
    }

    @Test
    void updateUser_UsernameTooShortTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        newUser.setUsername("short");
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidInputException.class, () -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("Username must be at least 6 characters long", result.getMessage());
    }

    @Test
    void updateUser_NullRoleProvidedTest() {
        var user = getUserEntity();
        var newUser = getUserEntity();
        newUser.setRole(null);
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.save(any(User.class)) ).thenAnswer( invocation -> invocation.getArgument(0) );
        assertEquals(Role.USER, user.getRole());

        var result = assertDoesNotThrow(() -> userService.updateUser(user.getId(), newUser));

        verify( userRepository, times(1) ).save(any(User.class));
        assertEquals(Role.USER, result.getRole());
    }

    @Test
    void getUserByUsernameTest() {
        var user = getUserEntity();
        when( userRepository.findByUsername(user.getUsername()) ).thenReturn(java.util.Optional.of(user));

        var result = assertDoesNotThrow( () -> userService.getUserByUsername(user.getUsername()) );

        verify( userRepository, times(1) ).findByUsername(user.getUsername());
        assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    void getUserByEmailTest() {
        var user = getUserEntity();
        when( userRepository.findByEmail(user.getEmail()) ).thenReturn(java.util.Optional.of(user));

        var result = assertDoesNotThrow( () -> userService.getUserByEmail(user.getEmail()) );

        verify( userRepository, times(1) ).findByEmail(user.getEmail());
        assertEquals(user.getEmail(), result.get().getEmail());
    }

    @Test
    void authenticateUserTest() {
        var user = getUserEntity();
        String password = "password";
        user.setPassword(passwordEncoder.encode( password ));
        when( userRepository.findByUsername(user.getUsername()) ).thenReturn(java.util.Optional.of(user));

        var result = assertDoesNotThrow( () -> userService.authenticateUser(user.getUsername(), password) );

        verify( userRepository, times(1) ).findByUsername(user.getUsername());
        assertEquals( user.getId(), result.getId() );
    }

    @Test
    void authenticateUser_UserNotFoundExceptionTest() {
        var user = getUserEntity();
        String password = "password";
        when( userRepository.findByUsername(user.getUsername()) ).thenReturn(java.util.Optional.empty());

        var result = assertThrows(UserNotFoundException.class, () -> userService.authenticateUser(user.getUsername(), password));

        verify( userRepository, times(1) ).findByUsername(user.getUsername());
        assertEquals("User with username " + user.getUsername() + " not found", result.getMessage());
    }

    @Test
    void authenticateUser_InvalidPasswordTest() {
        var user = getUserEntity();
        String password = "password";
        user.setPassword(passwordEncoder.encode( password ));
        when( userRepository.findByUsername(user.getUsername()) ).thenReturn(java.util.Optional.of(user));

        var result = assertThrows(InvalidPasswordException.class, () -> userService.authenticateUser(user.getUsername(), "invalid"));

        verify( userRepository, times(1) ).findByUsername(user.getUsername());
        assertEquals("Invalid password for username " + user.getUsername(), result.getMessage());
    }

    @Test
    void changeUserRoleTest() {
        var user = getUserEntity();
        user.setRole(Role.ADMIN);
        when( userRepository.existsById(user.getId()) ).thenReturn(true);
        when( userRepository.findById(user.getId()) ).thenReturn(java.util.Optional.of(user));
        when( userRepository.save(any(User.class)) ).thenAnswer( invocation -> invocation.getArgument(0) );

        var result = assertDoesNotThrow( () -> userService.changeUserRole(user.getId(), Role.ADMIN) );

        verify( userRepository, times(1) ).save(any(User.class));
        assertEquals(Role.ADMIN, result.getRole());
    }

    @Test
    void changeUserRole_UserNotFoundExceptionTest() {
        var user = getUserEntity();
        when( userRepository.existsById(user.getId()) ).thenReturn(false);

        var result = assertThrows(ResourceNotFoundException.class, () -> userService.changeUserRole(user.getId(), Role.ADMIN));

        verify( userRepository, times(0) ).save(any(User.class));
        assertEquals("User not found", result.getMessage());
    }
}
