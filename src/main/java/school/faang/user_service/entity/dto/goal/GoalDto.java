package school.faang.user_service.entity.dto.goal;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoalDto {
    private Long id;
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> skillIds;
}
