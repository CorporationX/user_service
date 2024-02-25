package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventStartDateFilterAfter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getStartDatePatternAfter() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event -> event.getStartDate().isAfter(eventFilterDto.getStartDatePatternAfter()));
    }
}
