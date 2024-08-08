package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventTitleFieldFilter implements EventFieldFilter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getTitle() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filter) {
        return eventStream.filter(event -> !filter.getTitle().isEmpty() && event.getTitle().contains(filter.getTitle()));
    }
}
