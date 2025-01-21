package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class EventTypeFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventType() != null && Arrays.asList(EventType.values()).contains(filter.getEventType());
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getType().getMessage().contains(filter.getEventType().getMessage()));
    }
}
