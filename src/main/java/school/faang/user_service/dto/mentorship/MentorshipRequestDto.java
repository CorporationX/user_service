package school.faang.user_service.dto.mentorship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;

public record MentorshipRequestDto(
        @NotNull(message = "ID cannot be null")
        Long id,
        @NotBlank
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
