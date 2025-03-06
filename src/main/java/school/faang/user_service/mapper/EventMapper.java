package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "owner", ignore = true)
    @Mapping(source = "ownerId", target = "owner.id")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    void update(EventDto eventDto, @MappingTarget Event event);


    List<EventDto> toDto(List<Event> list);
    List<EventDto> toDtoList(List<Event> events);
}
