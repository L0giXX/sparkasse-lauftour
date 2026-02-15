package at.marc.lauftour.services.impl;

import at.marc.lauftour.domain.entities.Runner;
import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.domain.entities.UserRunner;
import at.marc.lauftour.repositories.ResultRepository;
import at.marc.lauftour.repositories.RunnerRepository;
import at.marc.lauftour.repositories.UserRepository;
import at.marc.lauftour.repositories.UserRunnerRepository;
import at.marc.lauftour.services.UserRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRunnerServiceImpl implements UserRunnerService {
    private final RunnerRepository runnerRepository;
    private final UserRunnerRepository userRunnerRepository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    @Override
    public void bindOrCreateRunnerForUser(User user) {
        Runner runner = runnerRepository
                .findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
                        user.getFirstName(), user.getLastName(), user.getBirthDate()
                )
                .orElseGet(() -> {
                    Runner r = new Runner();
                    r.setFirstName(user.getFirstName());
                    r.setLastName(user.getLastName());
                    r.setBirthDate(user.getBirthDate());
                    return runnerRepository.save(r);
                });
        if (userRunnerRepository.existsByUser_IdAndRunner_Id(user.getId(), runner.getId())) {
            UserRunner link = new UserRunner();
            link.setUser(user);
            link.setRunner(runner);
            link.setStatus("VERIFIED");
            userRunnerRepository.save(link);
        }
    }

    @Override
    public long countRacesForUser(UUID userId) {
        return resultRepository.countDistinctRunsByUserId(userId);
    }

    @Override
    public int bindUsersForBatch(UUID importBatchId) {
        List<UUID> runnerIds = resultRepository.findDistinctRunnerIdsByImportBatchId(importBatchId);
        if (runnerIds.isEmpty()) return 0;

        // runners laden (Batch-weise)
        List<Runner> runners = runnerRepository.findAllById(runnerIds);

        int bound = 0;
        for (Runner runner : runners) {
            var opt = userRepository.findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
                    runner.getFirstName(), runner.getLastName(), runner.getBirthDate()
            );
            if (opt.isEmpty()) continue;
            User user = opt.get();

            if (userRunnerRepository.existsByUser_IdAndRunner_Id(user.getId(), runner.getId())) {
                UserRunner link = new UserRunner();
                link.setUser(user);
                link.setRunner(runner);
                link.setStatus("VERIFIED");
                userRunnerRepository.save(link);
                bound++;
            }
        }

        return bound;
    }
}
