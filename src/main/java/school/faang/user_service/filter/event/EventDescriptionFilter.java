package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

@Component
public class EventDescriptionFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getDescription() != null;
    }

    @Override
    public void apply(List<EventDto> eventDtos, EventFilterDto filter) {
        eventDtos.removeIf(eventDto -> !eventDto.getDescription().contains(filter.getDescription()));
    }
}
