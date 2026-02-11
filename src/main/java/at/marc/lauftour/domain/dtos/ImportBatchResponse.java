package at.marc.lauftour.domain.dtos;

import at.marc.lauftour.domain.entities.ImportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImportBatchResponse {
    private String source;
    private String fileName;
    private ImportStatus status;
    private int createdResults;
}
