package school.faang.user_service.dto.googleCalendar;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleEventDto {
    @NotNull
    private Long userId;
    @NotNull
    private Long eventId;
}
