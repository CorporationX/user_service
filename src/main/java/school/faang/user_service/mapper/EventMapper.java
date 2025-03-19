package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "eventDto.id", target = "id")
    @Mapping(source = "eventDto.eventType", target = "type")
    @Mapping(source = "eventDto.eventStatus", target = "status")
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Event toEntity(EventDto eventDto, User owner, List<Skill> relatedSkills);

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "relatedSkills", expression = "java(mapRelatedSkillsToIds(event.getRelatedSkills()))")
    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    EventDto toDto(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "eventDto.eventType", target = "type")
    @Mapping(source = "eventDto.eventStatus", target = "status")
    @Mapping(target = "relatedSkills", source = "relatedSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(EventDto eventDto, @MappingTarget Event event, User owner, List<Skill> relatedSkills);

    default List<Long> mapRelatedSkillsToIds(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }
}
