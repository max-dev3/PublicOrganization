package com.example.backend.controller;

import com.example.backend.dto.RoleDto;
import com.example.backend.exception.InvalidPasswordException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.exception.UserNotFoundException;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.request.UserLoginRequest;
import com.example.backend.response.ErrorResponse;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) throws ResourceNotFoundException {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User updatedUser)
            throws ResourceNotFoundException {
        User user = userService.updateUser(id, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest) {
        try {
            User user = userService.authenticateUser(userLoginRequest.getUsername(), userLoginRequest.getPassword());
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException | InvalidPasswordException ex) {
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }


//    @PutMapping("/{id}/role")
//    public ResponseEntity<User> changeUserRole(@PathVariable Long id, @Valid @RequestBody RoleDto roleDto)
//            throws Exception, ResourceNotFoundException {
//        Role userRole;
//        try {
//            userRole = Role.valueOf(roleDto.getRole());
//        } catch (IllegalArgumentException e) {
//            throw new Exception("Invalid role: " + roleDto.getRole());
//        }
//        User user = userService.changeUserRole(id, userRole);
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }

}
