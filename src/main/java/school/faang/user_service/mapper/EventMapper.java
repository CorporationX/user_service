package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillsIds", qualifiedByName = "getSkillId")
    EventDto toDto(Event event);

    Event toEntity(EventDto eventDto);

    List<EventDto> toEventsDto(List<Event> events);

    List<Event> toEvents(List<EventDto> eventsDto);

    @Named("getOwner")
    default User getOwner(Long ownerId) {
        User user = new User();
        user.setId(ownerId);
        return user;
    }

    @Named("getOwnerId")
    default Long getOwnerId(User user) {
        return user.getId();
    }

    @Named("getSkillId")
    default List<Long> getSkillId(List<Skill> skills) {
        return skills.stream().map(Skill::getId).toList();
    }

    @Named("getSkills")
    default List<Skill> getSkills(List<Long> skillIds) {
        List<Skill> skills = new ArrayList<>();
        skillIds.forEach(skillId -> skills.add(Skill.builder().id(skillId).build()));
        return skills;
    }
}
