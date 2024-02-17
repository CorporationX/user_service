package school.faang.user_service.mapper.google;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.google.EventCalendarDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventCalendarMapper {
    EventCalendarDto toDto(Event event);
}
