package com.example.backend.auth;

import com.example.backend.exception.InvalidInputException;
import com.example.backend.jwt.JwtService;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;  // Додаємо UserService для роботи з користувачами
    private final UserRepository userRepository; // Додаємо UserRepository для перевірки унікальності
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse register(RegistrationRequest request) {
        // Перевіряємо унікальність і коректність даних користувача
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidInputException("Email " + request.getEmail() + " already exists");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new InvalidInputException("Username " + request.getUsername() + " already exists");
        }

        // Створюємо нового користувача
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setRole(Role.USER); // Роль за замовчуванням

        // Зберігаємо нового користувача за допомогою UserService
        userService.createUser(newUser);

        // Створюємо UserDetails для генерації токен
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
        String jwt = jwtService.generateJwt(userDetails);

        return AuthenticationResponse.builder().token(jwt).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Аутентифікація користувача через AuthenticationManager
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Завантажуємо користувача для генерації токена
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String jwt = jwtService.generateJwt(userDetails);

        return AuthenticationResponse.builder().token(jwt).build();
    }
}
