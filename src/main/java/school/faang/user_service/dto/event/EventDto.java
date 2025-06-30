package school.faang.user_service.dto.event;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RequiredArgsConstructor
public class EventDto {
    private Long id;

    @NotBlank(message = "The event title must not be empty")
    private String title;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    private String description;
    private List<Long> relatedSkills;
    private String location;
    private int maxAttendees;
    private EventType eventType;
    private EventStatus eventStatus;
}
