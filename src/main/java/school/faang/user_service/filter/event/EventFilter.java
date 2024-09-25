package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.DtoFilter;

public interface EventFilter extends DtoFilter<EventFilterDto, Event> {
}
