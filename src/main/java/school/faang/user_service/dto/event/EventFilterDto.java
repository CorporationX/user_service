package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
