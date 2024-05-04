package school.faang.user_service.service.event.filter;

import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filters);

    Stream<Event> apply(Stream<Event> users, EventFilterDto filters);
}