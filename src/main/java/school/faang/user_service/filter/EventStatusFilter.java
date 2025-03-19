package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventStatusFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getEventStatus() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event -> event.getStatus().equals(eventFilterDto.getEventStatus()));
    }
}

