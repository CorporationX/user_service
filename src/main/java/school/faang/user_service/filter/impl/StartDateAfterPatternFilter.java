package school.faang.user_service.filter.impl;

import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.stream.Stream;

public class StartDateAfterPatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getStartDateAfterPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters) {
        return eventStream.filter(event -> event.getStartDate().isAfter(filters.getStartDateAfterPattern()));
    }

    @Override
    public boolean test(Event event, EventFilterDto filters) {
        return event.getStartDate().isAfter(filters.getStartDateAfterPattern());
    }
}
