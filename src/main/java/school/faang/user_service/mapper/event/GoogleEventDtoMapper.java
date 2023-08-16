package school.faang.user_service.mapper.event;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.GoogleEventDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleEventDtoMapper {

    @Mapping(target = "summary", source = "title")
    @Mapping(target = "start", source = "startDate", qualifiedByName = "mapEventDateTime")
    @Mapping(target = "end", source = "endDate", qualifiedByName = "mapEventDateTime")
    GoogleEventDto toGoogleEventDto(school.faang.user_service.entity.event.Event event);

    @Named("mapEventDateTime")
    default EventDateTime mapEventDateTime(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        DateTime dateTime = new DateTime(zonedDateTime.toInstant().toEpochMilli());

        return new EventDateTime().setDateTime(dateTime);
    }
}
