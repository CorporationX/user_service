package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilters eventFilters) {
        return eventFilters.getLocation() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilters eventFilters) {
        return eventStream.filter(event -> event.getLocation().equalsIgnoreCase(eventFilters.getLocation()));
    }
}
