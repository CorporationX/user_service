package school.faang.user_service.filter.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;
import java.util.Objects;

@Component
public class EventIdFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filter) {
        return filter.getId() != null;
    }

    @Override
    public void apply(List<EventDto> eventDtos, EventFilterDto filter) {
        eventDtos.removeIf(eventDto -> !Objects.equals(eventDto.getId(), filter.getId()));
    }
}
