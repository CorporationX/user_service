package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;

@Builder
@Data
public class UserDto {
    private Long id;
    boolean active;
    List<Goal> goals;
    List<Event> events;
}
