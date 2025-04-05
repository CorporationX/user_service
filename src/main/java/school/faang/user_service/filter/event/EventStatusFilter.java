package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventStatusFilter implements EventFilter {
    //STATUS;

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventStatusPattern() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getStatus() == filter
                .getEventStatusPattern());
    }
}
