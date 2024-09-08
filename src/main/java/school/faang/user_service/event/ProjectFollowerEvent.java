package school.faang.user_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectFollowerEvent {
    private long projectId;
    private long followerId;
    private long authorId;
}
