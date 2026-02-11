package at.marc.lauftour.services;

import at.marc.lauftour.domain.dtos.ImportBatchResponse;
import org.springframework.web.multipart.MultipartFile;


public interface ImportService {
    ImportBatchResponse importExcel(MultipartFile file);

    String deleteFile(String fileName);
}
