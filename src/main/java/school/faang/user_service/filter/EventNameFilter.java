package school.faang.user_service.filter;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
@Validated
public class EventNameFilter implements EventFilter {

    @Override
    public boolean isApplicable(@Valid EventFilterDto eventFilterDto) {
        return eventFilterDto.getEventTitle() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto eventFilterDto) {
        return events.filter(event -> event.getTitle().contains(eventFilterDto.getEventTitle()));
    }
}
