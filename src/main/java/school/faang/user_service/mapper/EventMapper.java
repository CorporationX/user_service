package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toDtoList(List<Event> events);
}
