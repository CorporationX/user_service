package school.faang.user_service.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;

import java.time.LocalDateTime;
import java.util.List;


public record EventDto(
        Long id,
        @NotBlank(message = "Title of event can't be blank")
        @Size(max = 50, message = "Size of title of event can't be exceed 50 characters")
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull(message = "Id of owner can't be null")
        @PositiveOrZero(message = "Id of owner can't be negative")
        Long ownerId,
        @NotNull(message = "Description must be")
        @NotBlank(message = "Description must be not blank")
        String description,
        @Size(min = 1, message = "Minimum 1 skill in event")
        List<Long> relatedSkills,
        @NotNull(message = "Location must be")
        @NotBlank(message = "Location can't be blank")
        @Size(max = 50, message = "Size of location's title of event can't be exceed 50 characters")
        String location,
        @Min(value = 1, message = "Minimum 1 participant")
        int maxAttendees,
        EventType eventType,
        EventStatus eventStatus
) {
}
