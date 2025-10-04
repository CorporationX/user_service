package school.faang.user_service.mapper;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;

@Component
@Mapper
public interface EventMapper {
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

    void update(UpdateEventDto eventDto, Event event);
}
