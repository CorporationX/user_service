package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.Setter;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("startDate")
    private LocalDateTime startDate;

    @JsonProperty("endDate")
    private LocalDateTime endDate;

    @JsonProperty("ownerId")
    private Long ownerId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("relatedSkills")
    private List<SkillDto> relatedSkills;

    @JsonProperty("location")
    private String location;

    @JsonProperty("maxAttendees")
    private int maxAttendees;
}
