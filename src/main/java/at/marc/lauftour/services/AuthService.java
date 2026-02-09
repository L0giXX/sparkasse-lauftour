package at.marc.lauftour.services;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;

public interface AuthService {
    UserDetails authenticate(String email, String password);

    String generateToken(UserDetails userDetails);

    UserDetails validateToken(String token);

    UserDetails register(String email, String password, String firstName, String lastName, LocalDate dateOfBirth);
}
