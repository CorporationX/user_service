package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventFilterMapper {
    @Mapping(target = "ownerId", source = "owner", qualifiedByName = "mapOwnerToOwnerId")
    @Mapping(target = "relatedSkillIds", source = "relatedSkills",
            qualifiedByName = "mapRelatedSkillsToTds")
    EventFilterDto eventToEventFilterDto(Event event);

    @Named("mapOwnerToOwnerId")
    default Long map(User user) {
        return user.getId();
    }

    @Named("mapRelatedSkillsToTds")
    default List<Long> map2(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
