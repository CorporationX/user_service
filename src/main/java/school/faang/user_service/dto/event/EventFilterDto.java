package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
public class EventFilterDto {
    private String eventTitle;
}
