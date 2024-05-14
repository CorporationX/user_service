package school.faang.user_service.dto.goal;

import lombok.Data;

import java.util.List;

@Data
public class GoalDto {
    private long id;
    private String title;
    private String description;
    private Long parentId;
    private String status;
    private List<Long> skillIds;
}
