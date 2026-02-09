package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.ImportBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, UUID> {
}