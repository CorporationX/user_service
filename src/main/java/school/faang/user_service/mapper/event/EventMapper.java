package school.faang.user_service.mapper.event;

import org.mapstruct.*;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "relatedSkills", target = "skillIds", qualifiedByName = "mapToSkillIds")
    @Mapping(source = "owner.id", target = "ownerId")
    EventDto toDto(Event event);

    @Mapping(target = "relatedSkills", ignore = true)
    Event toEvent(EventDto eventDto);

    @Named("mapToSkillIds")
    default List<Long> mapToSkillIds(List<Skill> relatedSkills) {
        return Optional.ofNullable(relatedSkills)
                .map(skills -> skills.stream().map(Skill::getId).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}