package at.marc.lauftour.services;

import at.marc.lauftour.domain.entities.ImportBatch;
import org.springframework.web.multipart.MultipartFile;

public interface ImportService {
    ImportBatch importExcel(MultipartFile file);

    String deleteFile(String fileName);
}
