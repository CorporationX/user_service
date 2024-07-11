package school.faang.user_service.service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

@Component
class EventTypeFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getTypePattern() != null && !filters.getTypePattern().isBlank();
    }

    @Override
    public Stream<Event> apply(List<Event> events, EventFilterDto filters) {
        return events.stream()
                .filter(event -> event.getType().getMessage().contains(filters.getTypePattern()));
    }
}