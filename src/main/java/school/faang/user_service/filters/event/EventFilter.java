package school.faang.user_service.filters.event;

import school.faang.user_service.dto.event.participant.RegisterParticipantDto;

public interface EventFilter {
    boolean isParticipant (RegisterParticipantDto registerParticipantDto);
}
