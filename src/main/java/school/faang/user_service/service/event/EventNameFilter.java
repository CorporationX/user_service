package school.faang.user_service.service.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import java.util.stream.Stream;

@Component
public class EventNameFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getEventTitle() != null;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        events.filter(event -> event.getTitle().contains(eventFilterDto.getEventTitle()));
    }
}
