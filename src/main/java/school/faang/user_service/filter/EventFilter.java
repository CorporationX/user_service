package school.faang.user_service.filter;

import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filters);

    Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters);
    boolean test(Event event, EventFilterDto filters);
}
