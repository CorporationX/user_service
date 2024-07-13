package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Collectors;
import java.util.stream.Stream;
@Component
public class EventByOwnerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getOwnerIdPattern() != null;
    }
    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        events.filter(event ->
                event.getOwner().getId()==(filters.getOwnerIdPattern()))
                .collect(Collectors.toList());
        return events;
    }
}