package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
public class EventFilterDto {
    private String eventTitle;
    private String eventType;
}
