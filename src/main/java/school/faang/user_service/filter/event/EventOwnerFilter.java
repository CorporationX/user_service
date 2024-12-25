package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.model.event.Event;

import java.util.stream.Stream;

@Component
public class EventOwnerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.ownerId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> allEvents, EventFilterDto filters) {
        return allEvents.filter(event -> event.isSameOwnerById(filters.ownerId()));
    }
}