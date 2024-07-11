package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFilterDto {
    private String titlePattern;
    private String descriptionPattern;
    private LocalDateTime startDatePattern;
    private LocalDateTime endDatePattern;
    private String locationPattern;
    private String skillPattern;
    private String typePattern;
    private String statusPattern;

}
