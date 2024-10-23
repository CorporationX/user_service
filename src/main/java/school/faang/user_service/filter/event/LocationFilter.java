package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
class LocationFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getLocation() != null && !filters.getLocation().isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(event -> event.getLocation().contains(filters.getLocation()));
    }
}