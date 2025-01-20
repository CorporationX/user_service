package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    Stream<Event> apply(Stream<Event> events, EventFilterDto filter);
}
