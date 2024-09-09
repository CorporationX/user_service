package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapper;

@Mapper(componentModel = "spring", uses = {SkillMapper.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    Event toEntity(EventDto eventDto);

}
