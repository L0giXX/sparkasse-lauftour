package at.marc.lauftour.services;

import at.marc.lauftour.domain.entities.User;

public interface UserRunnerService {
    void bindOrCreateRunnerForUser(User user);
}
