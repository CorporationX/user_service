package school.faang.user_service.dto.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDto(
        @NotNull(message = "Event ID can't be null")
        @Positive
        Long id,
        @NotBlank(message = "Event title can't be null or empty")
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull(message = "Event owner ID can't be null")
        @Positive
        Long ownerId,
        @NotBlank(message = "Event description can't be null or empty")
        String description,
        List<SkillDto> relatedSkills,
        String location,
        @NotNull(message = "maxAttendees can't be null")
        @Positive
        int maxAttendees) {
}
