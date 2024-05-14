package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import school.faang.user_service.entity.goal.GoalStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GoalDto {

    private Long id;

    @NotBlank(message = "Description cannot be blank")
    @Length(max = 128, message = "Description cannot be longer than 128 characters")
    private String description;

    @NotBlank(message = "Title cannot be blank")
    @Length(max = 64, message = "Title cannot be longer than 64 characters")
    private String title;

    private GoalStatus status = GoalStatus.ACTIVE;

    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    private Long parentId;

    private Long mentorId;

    @NotNull(message = "Skill IDs list cannot be null")
    private List<Long> skillIds;

    private List<Long> userIds;
}
