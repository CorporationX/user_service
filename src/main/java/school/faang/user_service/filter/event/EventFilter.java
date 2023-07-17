package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;

import java.util.List;

public interface EventFilter {
    boolean isApplicable(EventFilterDto filter);

    void apply(List<EventDto> eventDtos, EventFilterDto filter);
}
