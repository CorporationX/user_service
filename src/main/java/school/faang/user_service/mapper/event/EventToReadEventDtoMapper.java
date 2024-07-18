package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.ReadEvetDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventToReadEventDtoMapper {

     ReadEvetDto map(Event event);
}
