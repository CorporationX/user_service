package school.faang.user_service.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GoalDto {
    private Long id;
    private Long parentId;
    private RequestStatus status;
    private GoalStatus goalStatus;
    private String title;
    private String description;
    private List<Long> skillIds;

    public GoalDto(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}