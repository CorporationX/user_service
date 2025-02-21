package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Positive(message = "Owner ID must be positive")
    private Long ownerId;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<@Positive(message = "Skill ID must be positive") Long> relatedSkills;

    private String location;

    private Integer maxAttendees;

    @NotNull(message = "Event type is required")
    private EventType eventType;

    @NotNull(message = "Event status is required")
    private EventStatus eventStatus;
}
