package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.calendar.GoogleEventDto;
import school.faang.user_service.dto.event.EventDto;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleCalendarMapper {
    GoogleEventDto toGoogleEventDto(EventDto eventDto);
}
