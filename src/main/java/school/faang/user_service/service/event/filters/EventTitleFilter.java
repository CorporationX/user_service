package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilters eventFilters) {
        return eventFilters.getTitle() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilters eventFilters) {
        return eventStream.filter(event -> event.getTitle().contains(eventFilters.getTitle()));
    }
}
