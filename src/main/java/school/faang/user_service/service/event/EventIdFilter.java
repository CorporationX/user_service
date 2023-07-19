package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
public class EventIdFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getId() == filter.getEventId());
    }
}
