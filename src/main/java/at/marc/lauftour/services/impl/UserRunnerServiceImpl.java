package at.marc.lauftour.services.impl;

import at.marc.lauftour.domain.entities.Runner;
import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.domain.entities.UserRunner;
import at.marc.lauftour.repositories.RunnerRepository;
import at.marc.lauftour.repositories.UserRunnerRepository;
import at.marc.lauftour.services.UserRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRunnerServiceImpl implements UserRunnerService {
    private final RunnerRepository runnerRepository;
    private final UserRunnerRepository userRunnerRepository;

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
        if (!userRunnerRepository.existsByUser_IdAndRunner_Id(user.getId(), runner.getId())) {
            UserRunner link = new UserRunner();
            link.setUser(user);
            link.setRunner(runner);
            link.setStatus("VERIFIED");
            userRunnerRepository.save(link);
        }
    }
}
