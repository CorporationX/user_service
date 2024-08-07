package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;

import java.util.stream.Stream;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filters);

    Stream<Event> applyNotSafe(Stream<Event> events, EventFilterDto filters);

    default Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        if (filters == null) {
            throw new DataValidationException("EventFilterDto can't be null");
        }
        return applyNotSafe(events, filters);
    }


}
