package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.participant.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring" , unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "id", target = "eventId")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);
}
