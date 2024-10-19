package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import school.faang.user_service.model.enums.GoalStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto {
    private Long id;
    private Long parentId;
    @NonNull
    private String title;
    @NonNull
    private String description;
    private GoalStatus status;
    private List<Long> skillIds;
}
