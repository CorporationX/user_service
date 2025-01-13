package school.faang.user_service.dto.event.participant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NotNull
@NoArgsConstructor
@AllArgsConstructor
public class RegisterParticipantDto {
    private long userId;
    private long eventId;
}
