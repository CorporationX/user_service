package school.faang.user_service.dto.mentorship;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestDto(
        Long id,
        String description,
        UserDto requester,
        UserDto receiver,
        RequestStatus status
) {
}
