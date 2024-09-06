package school.faang.user_service.dto.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventFilters {
    private String title;
    private LocalDateTime startDate;
    private String location;
    private String ownerUsername;
}