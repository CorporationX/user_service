package school.faang.user_service.redis.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStartEvent {
    private Long id;
    private String title;
    private List<Long> attendeeIds;
}
