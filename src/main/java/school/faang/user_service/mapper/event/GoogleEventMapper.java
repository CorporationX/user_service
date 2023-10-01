package school.faang.user_service.mapper.event;

import com.google.api.services.calendar.model.Event;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.GoogleEventDto;


@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleEventMapper {
    Event toGoogleEvent(GoogleEventDto googleEventDto);
}

