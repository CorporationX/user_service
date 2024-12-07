package school.faang.user_service.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    private Long id;
    private Long parentId;

    @NotBlank(message = "Title must not be blank")
    @Size(max = 64, message = "Title must not exceed 64 characters")
    private String title;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 128, message = "Description must not exceed 128 characters")
    private String description;

    private String status;
    private List<Long> skillToAchieveIds;
}
