package school.faang.user_service.service.event.filters;

import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilters eventFilters);
    Stream<Event> apply(Stream<Event> eventStream, EventFilters eventFilters);

}
