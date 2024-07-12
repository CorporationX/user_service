package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

@Component
public class EventTitleFilter implements EventFilter {

    @Override
    public boolean apply(Event event, EventFilterDto filter) {
        return filter.getTitle() != null && !filter.getTitle().isEmpty() && event.getTitle().contains(filter.getTitle());
    }
}
