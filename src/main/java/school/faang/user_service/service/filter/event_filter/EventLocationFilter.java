package school.faang.user_service.service.filter.event_filter;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventFilter;

import java.util.stream.Stream;

public class EventLocationFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventDto) {
        return eventDto.getLocation() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventDto) {
        return events.filter(event -> event.getLocation().equals(eventDto.getLocation()));
    }
}
