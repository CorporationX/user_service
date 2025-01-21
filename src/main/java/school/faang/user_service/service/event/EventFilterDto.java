package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class EventFilterDto {
    private String title;
    private Long ownerId;
    private Long skillId;
    private Long startDate;
    private Long endDate;
    private Long minAttendees;
    private Long maxAttendees;
}
