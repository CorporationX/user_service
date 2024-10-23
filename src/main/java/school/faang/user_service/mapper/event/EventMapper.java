package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.event.EventDto;
import school.faang.user_service.model.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SkillMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

    List<EventDto> toListDto(List<Event> events);

}
