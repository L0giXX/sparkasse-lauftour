package at.marc.lauftour.services.impl;

import at.marc.lauftour.domain.entities.ImportBatch;
import at.marc.lauftour.domain.entities.ImportStatus;
import at.marc.lauftour.domain.entities.Result;
import at.marc.lauftour.domain.entities.Runner;
import at.marc.lauftour.repositories.ImportBatchRepository;
import at.marc.lauftour.repositories.ResultRepository;
import at.marc.lauftour.repositories.RunnerRepository;
import at.marc.lauftour.services.ImportService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    private final ImportBatchRepository batchRepository;
    private final RunnerRepository runnerRepository;
    private final ResultRepository resultRepository;

    @Override
    @Transactional
    public String deleteFile(String fileName) {
        if (!batchRepository.existsByFileName(fileName)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Import file not found");
        }
        batchRepository.deleteByFileName(fileName);
        return fileName;
    }

    @Override
    public ImportBatch importExcel(MultipartFile file) {
        ImportBatch batch = new ImportBatch();
        batch.setSource(file.getContentType());
        batch.setFileName(file.getOriginalFilename());
        batch.setImportStatus(ImportStatus.NEW);
        batch = batchRepository.save(batch);

        try (InputStream in = file.getInputStream(); Workbook wb = WorkbookFactory.create(in)) {
            Sheet sheet = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();

            // Header herauslesen (Lauftyp	Vorname	Nachname	Geburtsjahr	Email	Verein	Laufzeit)
            Map<String, Integer> idx = headerIndex(sheet.getRow(0), fmt);

            int rows = 0;
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String raceType = get(fmt, row, idx, "lauftyp");
                String firstName = get(fmt, row, idx, "vorname");
                String lastName = get(fmt, row, idx, "nachname");
                LocalDate birthDate = getDate(row, idx, "geburtsdatum", fmt);
                if (birthDate == null) {
                    String year = get(fmt, row, idx, "geburtsjahr");
                    if (year != null && !year.isBlank()) {
                        birthDate = LocalDate.of(Integer.parseInt(year.trim()), 1, 1);
                    }
                }

                String email = get(fmt, row, idx, "email");
                String club = get(fmt, row, idx, "verein");
                String time = get(fmt, row, idx, "laufzeit");

                if (isBlank(firstName) || isBlank(lastName) || birthDate == null || isBlank(raceType)) continue;

                LocalDate finalBirthDate = birthDate;

                // Neuen Läufer erstellen sofern Vorname, Nachname und Geburtsdatum unterschiedlich
                Runner runner = runnerRepository.
                        findByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(firstName.trim(), lastName.trim(), birthDate).
                        orElseGet(() -> {
                            Runner x = new Runner();
                            x.setFirstName(firstName.trim());
                            x.setLastName(lastName.trim());
                            x.setBirthDate(finalBirthDate);
                            x.setClub(club != null ? club.trim() : null);
                            return runnerRepository.save(x);
                        });
                Result res = new Result();
                res.setRunner(runner);
                res.setRaceType(raceType.trim());
                res.setDurationMs(parseDurationToMs(time));
                res.setParticipantEmail(email != null ? email.trim().toLowerCase(Locale.ROOT) : null);
                res.setClub(club != null ? club.trim() : null);
                res.setImportBatch(batch);

                resultRepository.save(res);
                rows++;
            }
            log.info("ImportBatch {} parsed rows={}", batch.getId(), rows);
            batch.setImportStatus(ImportStatus.PARSED);
            return batchRepository.save(batch);
        } catch (Exception e) {
            batch.setImportStatus(ImportStatus.FAILED);
            batch.setErrorMessage(e.getMessage());
            batchRepository.save(batch);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Import failed: " + e.getMessage());
        }
    }

    private static Map<String, Integer> headerIndex(Row header, DataFormatter fmt) {
        if (header == null) throw new IllegalArgumentException("Missing header row");
        Map<String, Integer> idx = new HashMap<>();
        for (Cell c : header) {
            String h = fmt.formatCellValue(c).trim().toLowerCase(Locale.ROOT);
            idx.put(h, c.getColumnIndex());
        }
        return idx;
    }

    // Zelle auslesen für spezifizierte Reihe und Spalte
    private static String get(DataFormatter fmt, Row row, Map<String, Integer> idx, String key) {
        Integer i = idx.get(key);
        if (i == null) return null;
        Cell c = row.getCell(i);
        return c == null ? null : fmt.formatCellValue(c);
    }

    // Zelle auslesen für spezifizierte Reihe und Spalte und Datum-Transformation
    private static LocalDate getDate(Row row, Map<String, Integer> idx, String key, DataFormatter fmt) {
        Integer i = idx.get(key);
        if (i == null) return null;
        Cell c = row.getCell(i);
        if (c == null) return null;

        if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
            return c.getLocalDateTimeCellValue().toLocalDate();
        }

        // fallback: parse string "YYYY-MM-DD" oder "DD.MM.YYYY"
        String s = fmt.formatCellValue(c).trim();
        if (s.isBlank()) return null;

        try {
            return LocalDate.parse(s);
        } catch (Exception ignored) {
        }
        try {
            DateTimeFormatter de = DateTimeFormatter.ofPattern("d.M.yyyy");
            return LocalDate.parse(s, de);
        } catch (Exception ignored) {
        }

        throw new IllegalArgumentException("Invalid birth date format: " + s);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static Integer parseDurationToMs(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        String[] parts = t.split(":");
        int h = 0, m = 0, sec = 0;
        if (parts.length == 3) {
            h = Integer.parseInt(parts[0]);
            m = Integer.parseInt(parts[1]);
            sec = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            m = Integer.parseInt(parts[0]);
            sec = Integer.parseInt(parts[1]);
        } else {
            sec = Integer.parseInt(parts[0]);
        }
        long total = h * 3600_000L + m * 60_000L + sec * 1000L;
        return total > Integer.MAX_VALUE ? null : (int) total;
    }
}
