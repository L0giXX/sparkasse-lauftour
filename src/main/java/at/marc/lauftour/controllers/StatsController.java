package at.marc.lauftour.controllers;

import at.marc.lauftour.domain.entities.User;
import at.marc.lauftour.services.UserRunnerService;
import at.marc.lauftour.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class StatsController {

    private final UserRunnerService userRunnerService;
    private final UserService userService;

    @GetMapping("/stats")
    public Map<String, Object> stats(Principal principal) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email);
        long races = userRunnerService.countRacesForUser(user.getId());
        return Map.of("racesParticipated", races);
    }
}
