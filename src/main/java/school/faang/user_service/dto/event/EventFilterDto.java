package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
    private String locationPattern;
    private int maxAttendeesPattern;
    private String ownerPattern;
    private String relatedSkillsPattern;

}
