package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

    void update(UpdateEventDto eventDto, @MappingTarget Event event);
}
