package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(uses = SkillMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    List<EventDto> toListDto(List<Event> events);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    List<Event> toListEntity(List<EventDto> eventDtoList);
}