package com.example.backend.auth;


import lombok.*;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class AuthenticationRequest {
    private String username;
    private String password;
}
