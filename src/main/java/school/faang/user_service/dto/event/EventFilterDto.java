package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFilterDto {
    private String titlePattern;
    private Long ownerId;
    private LocalDateTime startDate;
    private Long eventId;
}
