package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectFollowerEvent {
    private Long projectId;
    private Long followerId;
    private Long creatorId;

}
