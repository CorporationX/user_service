package school.faang.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {
    private Long id;
    private String description;
    private String name;
    private Long ownerId;
    private String coverImageId;
}
