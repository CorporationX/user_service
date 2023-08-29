package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
