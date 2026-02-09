package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.dtos.*;
import at.marc.lauftour.services.AuthService;
import at.marc.lauftour.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Lauftour!";
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        UserDetails userDetails = authService.register(registerRequest.getEmail(), registerRequest.getPassword(),
                registerRequest.getFirstName(), registerRequest.getLastName(), registerRequest.getDateOfBirth());
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        String token = authService.generateToken(userDetails);
        LoginResponse loginResponse = LoginResponse.builder().token(token).expiresIn(86400).build();
        return ResponseEntity.ok(loginResponse);
    }

    @PutMapping("/changepassword")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal principal) {
        String email = principal.getName();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        userService.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.noContent().build();
    }
}
