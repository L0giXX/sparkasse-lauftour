package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.dtos.ImportBatchResponse;
import at.marc.lauftour.services.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/imports")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping(value = "/excel", consumes = "multipart/form-data")
    public ImportBatchResponse importExcel(@RequestPart("file") MultipartFile file) {
        return importService.importExcel(file);
    }

    @DeleteMapping("/{fileName}")
    public String deleteFile(@PathVariable String fileName) {
        return importService.deleteFile(fileName);
    }
}
