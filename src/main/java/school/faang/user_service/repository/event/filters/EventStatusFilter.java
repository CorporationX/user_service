package school.faang.user_service.repository.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventStatusFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getStatusPattern() != null &&
                !filter.getStatusPattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getStatus().getMessage()
                .equalsIgnoreCase(filter.getStatusPattern());
    }
}
