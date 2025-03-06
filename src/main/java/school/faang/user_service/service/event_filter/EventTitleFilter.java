package school.faang.user_service.service.event_filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;
@Component
public class EventTitleFilter implements Filter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.title() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> event, EventFilterDto filter) {
        return event.filter(event1 -> event1.getTitle().equals(filter.title()));
    }
}