package school.faang.user_service.dto.project;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDto {
    @NotNull
    private Long projectId;

    @NotNull
    private Long ownerId;
}
