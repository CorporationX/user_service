package school.faang.user_service.dto.goal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GoalDTO {
    private Long id;

    private Long parentId;

    private String title;

    private String description;

    private String status;

    private LocalDateTime deadline;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long mentorId;

    private List<Long> userIds;

    private List<Long> skillToAchieveIds;
}
