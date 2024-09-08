package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SkillMapper.class}, unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface EventMapper {

    // TODO: finish event mapper

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", qualifiedByName = "skillsToSkillsDto")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    @Named("skillsToSkillsDto")
    static List<SkillDto> skillsToSkillsDto(List<Skill> skills) {
        return skills.stream().map(() -> )
    }
}
