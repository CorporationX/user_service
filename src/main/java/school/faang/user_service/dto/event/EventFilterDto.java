package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String descriptionPattern;
    private String startDatePattern;
    private String endDatePattern;
    private String locationPattern;
    private int maxAttendees;
    private long ownerId;
}
