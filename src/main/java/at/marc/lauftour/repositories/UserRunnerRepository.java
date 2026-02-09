package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.UserRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRunnerRepository extends JpaRepository<UserRunner, UUID> {
    boolean existsByUser_IdAndRunner_Id(UUID userId, UUID runnerId);

    List<UserRunner> findByUser_Id(UUID userId);
}

