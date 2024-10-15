package school.faang.user_service.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {
    private Long id;
    private String description;
    private String name;
    private Long ownerId;
    private String coverImageId;
}
