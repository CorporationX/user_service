package school.faang.user_service.service.filter.event_filter;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.service.event.EventFilter;

public class EventEndDataFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventDto eventDto) {
        return false;
    }

    @Override
    public EventDto apply(EventDto eventDto) {
        return null;
    }
}
