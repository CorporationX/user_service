package school.faang.user_service.service.event.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.event.Event;

@Component
public class EventDescriptionFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getDescriptionPattern() != null &&
                !filter.getDescriptionPattern().isBlank();
    }

    @Override
    public boolean applyFilter(Event event, EventFilterDto filter) {
        return event.getDescription().toLowerCase()
                .contains(filter.getDescriptionPattern().toLowerCase());

    }
}
