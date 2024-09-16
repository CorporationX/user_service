package school.faang.user_service.repository.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventMaxAttendeesLowerFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getMaxAttendeesLowerPattern() > 0;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getMaxAttendees() <= filter.getMaxAttendeesLowerPattern();
    }
}
