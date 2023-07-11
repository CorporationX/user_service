package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    Event toEvent(EventDto EventDto);
    EventDto toEventDto(Event Event);
}
