package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EventMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "relatedSkills", target = "relatedSkillIds", qualifiedByName = "toSkillIds")
    @Mapping(source = "attendees", target = "attendeeIds", qualifiedByName = "toAttendeeIds")
    EventDto toDto(Event event);

    @Mapping(source = "ownerId", target = "owner.id")
    Event toEntity(EventDto eventDto);

    @Named("toSkillIds")
    default List<Long> toSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("toAttendeeIds")
    default List<Long> toAttendeeIds(List<User> attendees) {
        return attendees.stream()
                .map(User::getId)
                .toList();
    }

    default List<EventDto> toListDto(List<Event> events) {
        return events.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    default List<Event> toListEntity(List<EventDto> events) {
        return events.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
