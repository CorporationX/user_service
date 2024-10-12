package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;

public interface EventService {
    EventDto getEvent(long id);
}