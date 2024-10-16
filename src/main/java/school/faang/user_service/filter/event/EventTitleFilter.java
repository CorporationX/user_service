package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.event.EventFilterDto;
import school.faang.user_service.model.entity.event.Event;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class EventTitleFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.titleFilter() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(event ->
                Objects.equals(event.getTitle(), filters.titleFilter()));
    }
}
