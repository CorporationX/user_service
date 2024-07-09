package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.stream.Stream;

@Component
public class MaxAttendeesFromPatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getMaxAttendeesFromPattern() > 0;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters) {
        return eventStream.filter(event -> event.getMaxAttendees() >= filters.getMaxAttendeesFromPattern());
    }

    @Override
    public boolean test(Event event, EventFilterDto filters) {
        return event.getMaxAttendees() >= filters.getMaxAttendeesFromPattern();
    }
}
