package at.marc.lauftour.repositories;

import at.marc.lauftour.domain.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ResultRepository extends JpaRepository<Result, UUID> {
    @Query("""
                select count(distinct r.importBatch.id)
                from Result r
                where r.runner.id in :runnerIds
                  and r.importBatch is not null
            """)
    long countDistinctImportBatchesByRunnerIds(@Param("runnerIds") List<UUID> runnerIds);

    @Query("select distinct r.runner.id from Result r where r.importBatch.id = :batchId")
    List<UUID> findDistinctRunnerIdsByImportBatchId(@Param("batchId") UUID batchId);


    List<Result> findTop500ByRunner_IdOrderByCreatedAtDesc(UUID runnerId);

    List<Result> findTop1000ByRunner_IdInOrderByCreatedAtDesc(List<UUID> runnerIds);
}
