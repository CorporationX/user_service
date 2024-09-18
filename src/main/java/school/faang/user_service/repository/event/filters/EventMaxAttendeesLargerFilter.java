package school.faang.user_service.repository.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventMaxAttendeesLargerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getMaxAttendeesLargerPattern() > 0;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getMaxAttendees() >= filter.getMaxAttendeesLargerPattern();
    }
}
