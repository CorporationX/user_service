package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;
import school.faang.user_service.dto.skill.SkillDto;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventDto {
    @NotNull
    Long id;
    @NotBlank
    String title;
    @NotNull
    LocalDateTime startDate;
    LocalDateTime endDate;
    @NotNull
    Long ownerId;
    @NotNull
    String description;
    List<SkillDto> relatedSkills;
    String location;
    int maxAttendees;
}
