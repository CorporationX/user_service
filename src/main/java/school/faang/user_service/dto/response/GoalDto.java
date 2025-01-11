package school.faang.user_service.dto.response;

import lombok.Getter;
import lombok.Setter;
import school.faang.user_service.dto.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class GoalDto {

    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private GoalStatus status;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> skillIds;

}
