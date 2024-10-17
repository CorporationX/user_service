package school.faang.user_service.filter.event;

import school.faang.user_service.model.entity.Event;
import school.faang.user_service.filter.DtoFilter;
import school.faang.user_service.model.filter_dto.EventFilterDto;

public interface EventFilter extends DtoFilter<EventFilterDto, Event> {
}
