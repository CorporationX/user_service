package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventDescriptionFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Event> applyNotSafe(Stream<Event> events, EventFilterDto filters) {
        return events
                .filter(event -> event.getDescription() != null)
                .filter(event -> event.getDescription().contains(filters.getDescriptionPattern()));
    }
}
