package school.faang.user_service.mapper.google;

import com.google.api.services.calendar.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.google.EventCalendarDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventCalendarMapper {
    EventCalendarDto toDto(Event event);
}
