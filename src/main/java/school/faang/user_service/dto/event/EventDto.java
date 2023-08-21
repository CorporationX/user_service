package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.event.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    @Min(value = 0, message = "Id should be a positive value")
    private Long id;
    @NotBlank(message = "Title cannot be blank")
    private String title;
    @NotNull(message = "Start Date cannot be null")
    private LocalDateTime startDate;
    @NotNull(message = "End Date cannot be null")
    private LocalDateTime endDate;
    @NotNull(message = "Owner cannot be null")
    private Long ownerId;
    @NotBlank(message = "Description cannot be blank")
    private String description;
    private List<SkillDto> relatedSkills;
    @NotNull(message = "Location cannot be null")
    private String location;
    @Min(value = 0, message = "Max Attendees should be a positive value")
    private int maxAttendees;
    @NotNull(message = "Type cannot be null")
    private EventType type;
    private EventStatus eventStatus;
}