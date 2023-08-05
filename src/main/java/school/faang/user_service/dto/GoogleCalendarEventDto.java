package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoogleCalendarEventDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private String location;
}
