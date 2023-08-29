package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.GoogleCalendarEventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleCalendarMapper {
    GoogleCalendarEventDto toDto(Event event);
}
