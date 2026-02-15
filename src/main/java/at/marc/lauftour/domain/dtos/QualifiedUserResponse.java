package at.marc.lauftour.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualifiedUserResponse extends UserResponse {
    private long races;
}
