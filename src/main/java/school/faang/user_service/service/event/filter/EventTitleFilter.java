package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class EventTitleFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getTitle() != null;
    }

    @Override
    public void apply(List<Event> events, EventFilterDto filters) {
        events.removeIf(event -> !event.getTitle().contains(filters.getTitle()));
    }
}
