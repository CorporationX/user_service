package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventStartEvent {
    private String title;
    private LocalDateTime startDate;
    private Long ownerId;
    private Long subscriberId;
}
