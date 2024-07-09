package school.faang.user_service.dto.event;

import lombok.Data;

@Data
public class EventFilterDto {
    private String descriptionPattern;
    private String locationPattern;
    private int maxAttendees;
    private long ownerId;
}
