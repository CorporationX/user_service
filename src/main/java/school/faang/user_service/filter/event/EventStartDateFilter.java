package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.EventFilterDto;
import school.faang.user_service.model.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventStartDateFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.startDateFilter() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filters) {
        return events.filter(event ->
                event.getStartDate().isEqual(filters.startDateFilter()));
    }
}
