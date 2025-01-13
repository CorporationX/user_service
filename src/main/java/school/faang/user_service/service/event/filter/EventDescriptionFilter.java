package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.stream.Stream;

@Component
public class EventDescriptionFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getDescription() != null && !filter.getDescription().isEmpty();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getDescription().contains(filter.getDescription()));
    }
}
