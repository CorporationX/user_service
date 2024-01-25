package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter{

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getLocationPattern()!=null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event -> event.getLocation().contains(eventFilterDto.getLocationPattern()));
    }
}
