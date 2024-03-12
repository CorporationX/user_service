package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

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
    private List<Long> skillIds;
}
