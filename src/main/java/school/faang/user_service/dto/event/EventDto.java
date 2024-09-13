package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode(of = {"relatedSkills"})
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    @Positive(message = "ID must be positive")
    private Long id;

    @NotBlank(message = "Event title cannot be blank.")
    private String title;

    @NotBlank(message = "Event description cannot be blank.")
    private String description;

    @NotNull(message = "Event start_date cannot be null.")
    @Future(message = "Event start_date must be in future.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "Event end_date cannot be null.")
    @Future(message = "Event end_date must be in future.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @NotBlank(message = "Event location cannot be blank.")
    private String location;

    @NotNull(message = "Event max_attendees cannot be null")
    private Integer maxAttendees;

    @Positive(message = "Owner_ID must be positive")
    private Long ownerId;

    @NotNull(message = "Event related_skills cannot be null.")
    private List<SkillDto> relatedSkills;

    @NotNull(message = "Event type cannot be null.")
    private EventType type;

    @NotNull(message = "Event status cannot be null.")
    private EventStatus status;
}
