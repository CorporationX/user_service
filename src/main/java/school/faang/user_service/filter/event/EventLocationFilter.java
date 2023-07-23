package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.stream.Stream;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocationPattern() != null;
    }

    @Override
    public Stream<EventDto> apply(Stream<EventDto> eventDtoStream, EventFilterDto filter) {
        return eventDtoStream.filter(eventDto -> eventDto.getLocation().matches(filter.getLocationPattern()));
    }
}
