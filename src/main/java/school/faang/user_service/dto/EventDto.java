package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import school.faang.user_service.model.event.EventStatus;
import school.faang.user_service.model.event.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDto(
        Long id,
        @NotBlank String title,
        @NotNull @PastOrPresent LocalDateTime startDate,
        @NotNull @PastOrPresent LocalDateTime endDate,
        @NotNull Long ownerId,
        @NotBlank String description,
        List<SkillDto> relatedSkills,
        @NotBlank String location,
        int maxAttendees,
        EventType type,
        EventStatus status
) {
    public String toLogString() {
        return String.format(("EventDto(id=%d, title=%s)"), id, title);
    }
}
