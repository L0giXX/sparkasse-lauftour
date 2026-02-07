package at.marc.lauftour.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private String token;
    private long expiresIn;
    private String email;
    private String firstName;
    private String lastName;
}
