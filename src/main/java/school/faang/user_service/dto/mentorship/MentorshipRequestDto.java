package school.faang.user_service.dto.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestDto(
        @Positive
        Long id,

        @NotNull
        String description,

        @NotNull @Valid
        UserDto requester,

        @NotNull @Valid
        UserDto receiver,

        @NotNull
        RequestStatus status
) {
}