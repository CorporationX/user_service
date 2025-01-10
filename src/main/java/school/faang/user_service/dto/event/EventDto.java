package school.faang.user_service.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Positive(message = "Owner ID must be positive")
    private Long ownerId;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private List<@Positive(message = "Skill ID must be positive") Long> relatedSkills;

    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}