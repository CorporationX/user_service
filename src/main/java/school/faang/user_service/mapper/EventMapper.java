package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "ownerId", target = "owner.id")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);
}
