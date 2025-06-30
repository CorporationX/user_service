package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class TitleEventFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getTitle() != null && !filter.getTitle().isBlank();
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filter) {
        return events.filter(event -> event.getTitle() != null
                && event.getTitle().toLowerCase().contains(filter.getTitle().toLowerCase()));
    }
}