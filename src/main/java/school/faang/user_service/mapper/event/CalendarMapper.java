package school.faang.user_service.mapper.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.CalendarEventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CalendarMapper {
    CalendarEventDto toDto(Event event);
}