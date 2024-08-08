package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventStartDateAfterFieldFilter implements EventFieldFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getStartDate() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filter) {
        return eventStream.filter(event -> event.getStartDate().isAfter(filter.getStartDate()));
    }
}

