package school.faang.user_service.service.event.filters;

import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto eventFilterDto);

    Stream<Event> apply(List<Event> eventList, EventFilterDto eventFilterDto);
}
