package school.faang.user_service.dto.event;

import com.google.api.services.calendar.model.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleEventDto {
    private String summary;
    private String description;
    private String location;
    private EventDateTime start;
    private EventDateTime end;
}
