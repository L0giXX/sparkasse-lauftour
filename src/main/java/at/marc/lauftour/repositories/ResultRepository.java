package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResultRepository extends JpaRepository<Result, UUID> {
    List<Result> findTop500ByRunner_IdOrderByCreatedAtDesc(UUID runnerId);

    List<Result> findTop1000ByRunner_IdInOrderByCreatedAtDesc(List<UUID> runnerIds);
}
