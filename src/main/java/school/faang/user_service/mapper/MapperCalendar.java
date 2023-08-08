package school.faang.user_service.mapper;

import com.google.api.services.calendar.model.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.EventCalendarDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MapperCalendar {
    EventCalendarDto toDto(Event event);
}
