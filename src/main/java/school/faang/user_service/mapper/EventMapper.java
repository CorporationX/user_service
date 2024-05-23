package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "mapRelatedSkillsIds")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    @Named("mapRelatedSkillsIds")
    default List<Long> mapRelatedSkillsIds(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }

    List<EventDto> toListDto(List<Event> events);

    default List<Event> toListEntity(List<EventDto> events) {
        return events.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
