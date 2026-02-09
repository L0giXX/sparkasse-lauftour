package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.Runner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RunnerRepository extends JpaRepository<Runner, UUID> {
    Optional<Runner> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
            String firstName, String lastName, LocalDate birthDate);
}
