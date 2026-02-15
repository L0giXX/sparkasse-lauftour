package at.marc.lauftour.services;

import at.marc.lauftour.domain.dtos.QualifiedUserResponse;
import at.marc.lauftour.domain.dtos.UserResponse;
import at.marc.lauftour.domain.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);

    User getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    List<User> getUsersWithAtLeastRuns(long minRuns);

    List<QualifiedUserResponse> getUsersWithAtLeastRunsAndRaceCount(long minRuns);

    void changePassword(String email, String oldPassword, String newPassword);
}
