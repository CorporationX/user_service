package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "status", target = "eventStatus")
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkills") // здесь нужен кастомный метод
    EventDto toDto(Event event);

    @Mapping(source = "eventType", target = "type")
    @Mapping(source = "eventStatus", target = "status")
    @Mapping(source = "ownerId", target = "owner.id")
    @Mapping(source = "relatedSkills", target = "relatedSkills")
    @Mapping(target = "attendees", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "participants", ignore = true)
    Event toEntity(EventDto eventDto);

    default List<Long> map(List<Skill> skills) {
        if (skills == null) {
            return null;
        }
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    default List<Skill> mapToSkills(List<Long> ids) {
        if (ids == null) {
            return null;
        }
        return ids.stream().map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
        }).collect(Collectors.toList());
    }

    void update(@MappingTarget Event event, EventDto dto);

}