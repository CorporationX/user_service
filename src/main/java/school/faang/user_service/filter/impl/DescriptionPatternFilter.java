package school.faang.user_service.filter.impl;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventFilterDto;

import java.util.stream.Stream;

@Component
public class DescriptionPatternFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters) {
        return eventStream.filter(event -> event.getDescription().toLowerCase()
                .contains(filters.getDescriptionPattern().toLowerCase()));
    }

    @Override
    public boolean test(Event event, EventFilterDto filters) {
        return event.getDescription().toLowerCase()
                .contains(filters.getDescriptionPattern().toLowerCase());
    }
}
