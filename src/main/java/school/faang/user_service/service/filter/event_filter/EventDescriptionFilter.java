package school.faang.user_service.service.filter.event_filter;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventFilter;

import java.util.stream.Stream;

public class EventDescriptionFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventDto) {
        return eventDto.getDescription() != null;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto eventDto) {
        events.filter(event -> event.getDescription().equals(eventDto.getDescription()));
    }
}
