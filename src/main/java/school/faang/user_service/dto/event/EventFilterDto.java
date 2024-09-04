package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@RequiredArgsConstructor
public class EventFilterDto {
    private String eventName;
    private String eventType;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;
    private String eventStatus;
}
