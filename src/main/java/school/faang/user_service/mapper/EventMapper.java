package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    EventDto toDto(Event event);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    Event toEntity(EventDto eventDto);

    default List<Long> mapSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    default List<Skill> mapIdsToSkills(List<Long> skillIds) {
        return null;
    }
}
