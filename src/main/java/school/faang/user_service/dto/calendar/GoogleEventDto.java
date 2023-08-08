package school.faang.user_service.dto.calendar;

import com.google.api.services.calendar.model.EventDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleEventDto {
    private String summary;
    private String description;
    private String location;
    private EventDateTime start;
    private EventDateTime end;
}
