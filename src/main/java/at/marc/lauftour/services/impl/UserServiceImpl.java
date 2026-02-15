package at.marc.lauftour.services.impl;

import at.marc.lauftour.domain.dtos.QualifiedUserResponse;
import at.marc.lauftour.domain.dtos.UserResponse;
import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.domain.entities.UserRole;
import at.marc.lauftour.repositories.UserRepository;
import at.marc.lauftour.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findByRoleNot(UserRole.ADMIN).stream().map(user -> {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setEmail(user.getEmail());
            userResponse.setFirstName(user.getFirstName());
            userResponse.setLastName(user.getLastName());
            userResponse.setBirthDate(user.getBirthDate());
            return userResponse;
        }).toList();
    }

    @Override
    public List<User> getUsersWithAtLeastRuns(long minRuns) {
        return userRepository.findUsersWithAtLeastRuns(minRuns, UserRole.ADMIN);
    }

    @Override
    public List<QualifiedUserResponse> getUsersWithAtLeastRunsAndRaceCount(long minRuns) {
        return userRepository.findQualifiedUsersWithRaceCount(minRuns, UserRole.ADMIN)
                .stream()
                .map(row -> {
                    QualifiedUserResponse response = new QualifiedUserResponse();
                    response.setId((UUID) row[0]);
                    response.setFirstName((String) row[1]);
                    response.setLastName((String) row[2]);
                    response.setEmail((String) row[3]);
                    response.setBirthDate((LocalDate) row[4]);
                    response.setRaces((Long) row[5]);
                    return response;
                })
                .toList();
    }


    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
