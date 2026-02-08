package at.marc.lauftour.services;

import at.marc.lauftour.domain.dtos.ChangePasswordRequest;
import at.marc.lauftour.domain.entities.User;

import java.security.Principal;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);

    void changePassword(String email, String oldPassword, String newPassword);

}
