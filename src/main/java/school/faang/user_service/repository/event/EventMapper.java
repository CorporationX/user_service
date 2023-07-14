package school.faang.user_service.repository.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;


@Mapper
@Component
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "id", ignore = true)
    Event toEvent(EventDto eventDto);

    EventDto toEventDto(Event event);

}
