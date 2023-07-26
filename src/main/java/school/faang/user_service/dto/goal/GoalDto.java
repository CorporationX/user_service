package school.faang.user_service.dto.goal;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class GoalDto {
    private Long id;
    private Long parentId;
    private String title;
    private String description;
    private List<Long> skillIds;
}
