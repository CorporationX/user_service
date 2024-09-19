package school.faang.user_service.dto.goal;

import lombok.*;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {

    private Long id;
    private Long parentId;
    @NonNull private String title;
    @NonNull private String description;
    private GoalStatus status;
    private List<Long> skillIds;
}
