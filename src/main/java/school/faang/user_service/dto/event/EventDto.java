package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.event.validation.ValidationGroups;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    @Null(message = "Id must be null for creation", groups = ValidationGroups.Create.class)
    @NotNull(message = "Id cannot be null for update", groups = ValidationGroups.Update.class)
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NotNull(message = "Owner cannot be null")
    @Positive(message = "Owner id cannot be negative or equals 0")
    private Long ownerId;
    private String description;
    private List<SkillDto> relatedSkills;
    private String location;
    private int maxAttendees;
    @NotNull(message = "Type cannot be null")
    private EventType type;
    @NotNull(message = "Status cannot be null")
    private EventStatus status;
}
