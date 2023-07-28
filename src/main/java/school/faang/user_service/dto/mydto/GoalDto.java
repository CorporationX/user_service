package school.faang.user_service.dto.mydto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private String description;
    private Long parentId;
    private String title;
    private GoalStatus status;
    private LocalDateTime deadline;
    private Long mentorId;
    private List<Long> skillIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
