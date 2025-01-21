package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NotNull
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantDto {
    private Long userId;
    private Long eventId;
}
