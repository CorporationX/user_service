package school.faang.user_service.repository.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventEndDateAfterFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getEndDateAfterPattern() != null;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getEndDate().isAfter(filter.getEndDateAfterPattern()) ||
                event.getEndDate().isEqual(filter.getEndDateAfterPattern());
    }
}
