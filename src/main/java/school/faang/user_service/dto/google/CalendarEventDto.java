package school.faang.user_service.dto.google;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalendarEventDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;
}
