package school.faang.user_service.dto.projectfollower;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectFollowerEvent {
    private Long followerId;
    private Long projectId;
    private Long creatorId;
}
