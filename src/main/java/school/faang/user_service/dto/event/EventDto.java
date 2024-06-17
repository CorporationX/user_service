package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;

    @NotBlank(message = "Event title cannot be blank.")
    private String title;

    @NotNull(message = "Event description cannot be null.")
    private String description;

    @NotNull(message = "Event start date cannot be null.")
    @Future(message = "The event start date cannot be in the past.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "Event end date cannot be null.")
    @Future(message = "The event end date cannot be in the past.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotNull(message = "Event ownerId cannot be null.")
    private Long ownerId;

    private List<SkillDto> relatedSkills;

    @NotNull(message = "Event location cannot be null.")
    private String location;

    @NotNull(message = "Event type cannot be null.")
    private EventType type;

    @NotNull(message = "Event status cannot be null.")
    private EventStatus status;
}
