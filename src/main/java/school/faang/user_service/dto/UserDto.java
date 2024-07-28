package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private boolean active;
    private List<Goal> goals;
    private List<Event> events;
}
