package school.faang.user_service.dto.event;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {
    @Null(message = "Event ID should be empty for create new Event", groups = Create.class)
    @NotNull(message = "Event ID can't be null", groups = Update.class)
    @Min(value = 1, groups = Update.class)
    private Long id;
    @NotBlank(message = "Event title can't be null or empty", groups = Create.class)
    @Size(max = 128, message = "Event title must not exceed 128 characters", groups = {Create.class, Update.class})
    private String title;
    @NotBlank(message = "Event description can't be null or empty", groups = Create.class)
    @Size(max = 4000, message = "Event description must not exceed 4000 characters", groups = {Create.class, Update.class})
    private String description;
    @NotNull(message = "Event start date can't be null", groups = Create.class)
    private LocalDateTime startDate;
    @NotNull(message = "Event end date can't be null", groups = Create.class)
    private LocalDateTime endDate;
    @NotBlank(message = "Event location can't be null or empty", groups = Create.class)
    @Size(max = 64, message = "Event location must not exceed 64 characters", groups = Create.class)
    private String location;
    @NotNull(message = "maxAttendees must be more then 0", groups = Create.class)
    @Min(value = 1, groups = {Create.class, Update.class})
    private Integer maxAttendees;
    @NotNull(message = "Event owner ID can't be null", groups = {Create.class, Update.class})
    @Min(value = 1, message = "Owner ID can't be less then 1", groups = {Create.class, Update.class})
    private Long ownerId;
    private List<SkillDto> relatedSkills;
    @NotNull(message = "Event type can't be null for create Event", groups = Create.class)
    @Enumerated(EnumType.STRING)
    private EventType type;
    @NotNull(message = "Event status can't be null for create Event", groups = Create.class)
    @Enumerated(EnumType.STRING)
    private EventStatus status;
}
