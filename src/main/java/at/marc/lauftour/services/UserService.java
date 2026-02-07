package at.marc.lauftour.services;

import at.marc.lauftour.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID id);
}
