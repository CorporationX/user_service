package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isAcceptable(EventFilterDto filterDto) {
        return filterDto.getLocation() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(event -> event.getLocation().equals(filters.getLocation()));
    }
}
