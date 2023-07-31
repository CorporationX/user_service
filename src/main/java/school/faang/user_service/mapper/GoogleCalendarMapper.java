package school.faang.user_service.mapper;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.calendar.GoogleEventDto;
import school.faang.user_service.dto.event.EventDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleCalendarMapper {
    @Mapping(source = "startDate", target = "startDate", qualifiedByName = "mapDate")
    @Mapping(source = "endDate", target = "endDate", qualifiedByName = "mapDate")
    @Mapping(source = "title", target = "summary")
    GoogleEventDto toGoogleEventDto(EventDto eventDto);

    @Mapping(source = "startDate", target = "start")
    @Mapping(source = "endDate", target = "end")
    Event toGoogleEvent(GoogleEventDto eventDto);

    @Named("mapDate")
    default EventDateTime mapDate(LocalDateTime date) {
        // Convert LocalDateTime to DateTime
        DateTime dateTime = new DateTime(Date.from(date.atZone(ZoneId.systemDefault()).toInstant()));

        EventDateTime eventDateTime = new EventDateTime();
        eventDateTime.setDateTime(dateTime);

        return eventDateTime;
    }
}
