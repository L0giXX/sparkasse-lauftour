package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.entities.ImportBatch;
import at.marc.lauftour.services.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/imports")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping(value = "/excel", consumes = "multipart/form-data")
    public ImportBatch importExcel(@RequestPart("file") MultipartFile file) {
        return importService.importExcel(file);
    }
}
