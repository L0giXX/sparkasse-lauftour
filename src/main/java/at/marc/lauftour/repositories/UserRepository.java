package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
            String firstName, String lastName, LocalDate birthDate
    );
}
