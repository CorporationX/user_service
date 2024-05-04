package school.faang.user_service.dto.filter;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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
