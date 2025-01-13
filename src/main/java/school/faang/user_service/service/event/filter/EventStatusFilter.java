package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.entity.event.Event;
import school.faang.user_service.dto.entity.event.EventStatus;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.Arrays;
import java.util.stream.Stream;

@Component
public class EventStatusFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEventStatus() != null && Arrays.asList(EventStatus.values()).contains(filter.getEventStatus());
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getStatus().getMessage().contains(filter.getEventStatus().getMessage()));
    }
}
