package school.faang.user_service.dto.goal;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
@NoArgsConstructor
public class GoalDto {

        private Long id;
        private Long parentId;
        private String title;
        private String description;
        private GoalStatus status;
        private LocalDateTime deadline;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Long mentorId;
        private List<Long> skillIds;
        private List<Long> userIds;
}