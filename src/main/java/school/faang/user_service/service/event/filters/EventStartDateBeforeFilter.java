package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventStartDateBeforeFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getStartDateBeforePattern() != null;
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getStartDate().isBefore(filter.getStartDateBeforePattern()) ||
                event.getStartDate().equals(filter.getStartDateBeforePattern());
    }
}
