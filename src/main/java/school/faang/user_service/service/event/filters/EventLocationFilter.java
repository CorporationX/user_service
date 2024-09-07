package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocationPattern() != null &&
                !filter.getLocationPattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getLocation().toLowerCase()
                .contains(filter.getLocationPattern().toLowerCase());

    }
}
