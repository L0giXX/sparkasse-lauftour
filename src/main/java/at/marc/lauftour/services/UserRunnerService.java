package at.marc.lauftour.services;

import at.marc.lauftour.domain.entities.User;

import java.util.UUID;

public interface UserRunnerService {
    void bindOrCreateRunnerForUser(User user);

    int bindUsersForBatch(UUID importBatchId);
    
    long countRacesForUser(UUID id);
}
