package school.faang.user_service.service.event.event_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventFilter;

import java.util.stream.Stream;

@Component
public class EventEndDataFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventDto) {
        return eventDto.getEndDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventDto) {
        return events.filter(event -> event.getEndDate().isAfter(eventDto.getEndDate()));
    }
}