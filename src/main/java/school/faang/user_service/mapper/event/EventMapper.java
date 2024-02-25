package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventUpdateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "skillIdToSkill")
    Event toEntity(EventDto eventDto);

    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "skillToIdSkill")
    EventDto toDto(Event event);


    @Mapping(source = "relatedSkills", target = "relatedSkills", qualifiedByName = "skillIdToSkill")
    Event toUpdateDto(EventUpdateDto eventUpdateDto);

    @Named("skillToIdSkill")
    default Long skillToIdSkill(Skill skill) {
        return skill.getId();
    }

    @Named("skillIdToSkill")
    default Skill skillIdToSkill(Long idSkill) {
        Skill skill = new Skill();
        skill.setId(idSkill);
        return skill;
    }

}
