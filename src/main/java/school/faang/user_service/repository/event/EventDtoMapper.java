package school.faang.user_service.repository.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;


@Mapper(componentModel = "spring")
@Component
public interface EventDtoMapper {
    EventDto mapToDto(Event event);
    Event mapToEntity(EventDto eventDto);

}
