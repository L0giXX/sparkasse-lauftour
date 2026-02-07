package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.dtos.AuthResponse;
import at.marc.lauftour.domain.dtos.LoginRequest;
import at.marc.lauftour.domain.dtos.RegisterRequest;
import at.marc.lauftour.domain.dtos.RegisterResponse;
import at.marc.lauftour.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Lauftour!";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        UserDetails userDetails = authService.register(registerRequest.getEmail(), registerRequest.getPassword(),
                registerRequest.getFirstName(), registerRequest.getLastName());
        String token = authService.generateToken(userDetails);
        RegisterResponse registerResponse = RegisterResponse.builder()
                .token(token).expiresIn(86400)
                .email(userDetails.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .build();
        return new ResponseEntity<>(registerResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String token = authService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder().token(token).expiresIn(86400).build();
        return ResponseEntity.ok(authResponse);
    }
}
