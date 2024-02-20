package school.faang.user_service.dto.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private LocalDateTime startDatePatternAfter;
    private LocalDateTime endDatePatternAfter;
    private LocalDateTime startDatePatternBefore;
    private LocalDateTime endDatePatternBefore;
    private String locationPattern;
    private int maxAttendeesPattern;
    private String ownerPattern;
    private String relatedSkillsPattern;

}
