package school.faang.user_service.dto;

import jakarta.validation.Valid;
import school.faang.user_service.dto.user.ParticipantDto;

public record RegisterRequest(
        @Valid
        ParticipantDto participantDto,
        @Valid
        EventDto eventDto
) {
}
