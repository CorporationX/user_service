package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

@Component
public class EventStartDateAfterFilter implements EventFilter {

    @Override
    public boolean apply(Event event, EventFilterDto filter) {
        return filter.getStartDate() != null && event.getStartDate().isAfter(filter.getStartDate());
    }
}
