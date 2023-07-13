package school.faang.user_service.service.event;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
  EventDto toDTO(Event model);
  Event toModel(EventDto dto);
}
