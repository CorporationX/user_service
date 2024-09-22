package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private int maxAttendees;
    private EventType type;
    private EventStatus status;
    private LocalDate createdAt;
}
