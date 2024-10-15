package school.faang.user_service.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EventDto(

        @Null(message = "Event ID can't be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long id,

        @NotBlank(message = "Event title can't be empty", groups = {CreateGroup.class, UpdateGroup.class})
        String title,

        @NotNull(message = "Start date can not be null", groups = {CreateGroup.class, UpdateGroup.class})
        LocalDateTime startDate,

        @NotNull(message = "End date can not be null", groups = {CreateGroup.class, UpdateGroup.class})
        LocalDateTime endDate,

        @NotNull(message = "Event owner ID can't be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Long ownerId,

        @NotBlank(message = "Event description can't be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        String description,

        List<SkillDto> relatedSkills,

        @NotBlank(message = "Location can not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        String location,
        
        @NotNull(message = "Max. attendees can't be null", groups = {CreateGroup.class, UpdateGroup.class})
        @Positive
        Integer maxAttendees
) {
}
