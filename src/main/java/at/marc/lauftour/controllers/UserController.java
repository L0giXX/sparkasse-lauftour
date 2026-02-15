package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.dtos.*;
import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.services.UserRunnerService;
import at.marc.lauftour.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserRunnerService userRunnerService;
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/by-min-runs")
    public ResponseEntity<List<QualifiedUserResponse>> getUsersByMinRuns(@RequestParam(defaultValue = "1") long minRuns) {
        if (minRuns < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minRuns must be at least 1");
        }
        return ResponseEntity.ok(userService.getUsersWithAtLeastRunsAndRaceCount(minRuns));
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        long races = userRunnerService.countRacesForUser(user.getId());
        return Map.of("racesParticipated", races);
    }
}
