package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.EventFilterDto;
import school.faang.user_service.model.entity.event.Event;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class EventOwnerIdFilter implements EventFilter{
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.ownerIdFilter() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(event ->
                Objects.equals(event.getOwner().getId(), filters.ownerIdFilter()));
    }
}
