package school.faang.user_service.dto;

import jakarta.validation.Valid;
import school.faang.user_service.dto.user.UserDto;

public record RegisterRequest(
        @Valid
        UserDto userDto,
        @Valid
        EventDto eventDto
) {
}
