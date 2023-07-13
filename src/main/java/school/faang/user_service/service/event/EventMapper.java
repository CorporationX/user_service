package school.faang.user_service.service.event;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

  @Mapper(componentModel = "spring")
public interface EventMapper {
  public EventDto toDto(Event event);

  public Event toEntity(EventDto eventDto);
}