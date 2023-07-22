package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    Stream<EventDto> apply(Stream<EventDto> eventDtoStream, EventFilterDto filter);
}
