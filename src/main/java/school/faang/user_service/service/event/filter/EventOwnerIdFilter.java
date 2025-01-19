package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class EventOwnerIdFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getOwnerId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> Objects.equals(event.getOwner().getId(), filter.getOwnerId()));
    }
}
