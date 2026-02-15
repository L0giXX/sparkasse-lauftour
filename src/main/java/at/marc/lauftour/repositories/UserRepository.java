package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.domain.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleNot(UserRole role);

    Optional<User> findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(
            String firstName, String lastName, LocalDate birthDate
    );

    @Query("""
            select u
            from User u
            join UserRunner ur on ur.user = u
            join Result r on r.runner = ur.runner
            where r.importBatch is not null
            and u.role <> :excludedRole
            group by u
            having count(distinct r.importBatch.id) >= :minRuns
            """)
    List<User> findUsersWithAtLeastRuns(
            @Param("minRuns") long minRuns,
            @Param("excludedRole") UserRole excludedRole
    );

    @Query("""
            select u.id, u.firstName, u.lastName, u.email, u.birthDate, count(distinct r.importBatch.id)
            from User u
            join UserRunner ur on ur.user = u
            join Result r on r.runner = ur.runner
            where r.importBatch is not null
            and u.role <> :excludedRole
            group by u.id, u.firstName, u.lastName, u.email, u.birthDate
            having count(distinct r.importBatch.id) >= :minRuns
            """)
    List<Object[]> findQualifiedUsersWithRaceCount(
            @Param("minRuns") long minRuns,
            @Param("excludedRole") UserRole excludedRole
    );
}
