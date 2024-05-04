package school.faang.user_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoalDto {
    private long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime deadline;
    private List<Long> skillsToAchieve;
}
