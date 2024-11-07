package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {

    @NotNull(groups = {After.class})
    private Long id;
    private Long parentId;
    @NotNull(groups = {Before.class})
    private String title;
    @NotNull(groups = {Before.class})
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
    private Long mentorId;
    private LocalDateTime deadline;

    public interface After {}

    public interface Before {}
}
