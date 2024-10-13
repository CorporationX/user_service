package school.faang.user_service.filter.event;

import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.EntityFilter;

public interface EventFilter extends EntityFilter<EventFilterDto, Event> {}
