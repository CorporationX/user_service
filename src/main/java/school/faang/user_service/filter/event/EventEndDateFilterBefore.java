package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public class EventEndDateFilterBefore implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getEndDatePatternBefore() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event -> event.getEndDate().isBefore(eventFilterDto.getEndDatePatternBefore()));
    }

}
