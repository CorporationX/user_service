package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

@Component
public class EventLocationFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getLocation() != null;
    }

    @Override
    public void apply(List<EventDto> eventDtoStream, EventFilterDto filter) {
        eventDtoStream.removeIf(eventDto -> !eventDto.getDescription().contains(filter.getDescription()));
    }
}
