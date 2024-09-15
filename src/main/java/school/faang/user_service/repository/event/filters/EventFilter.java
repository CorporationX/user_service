package school.faang.user_service.repository.event.filters;

import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    boolean applyFilter(Event event, EventFilterDto filter);
}
