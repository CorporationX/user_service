package school.faang.user_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;

@Setter
@Getter
public class CreateGoalResponseDto {

    private Long id;
    private Goal parent;
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long mentorId;

}
