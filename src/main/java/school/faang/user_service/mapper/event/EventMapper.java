package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class EventMapper {

    @Autowired
    private SkillRepository skillRepository;

    @Mapping(target = "relatedSkills", source = "relatedSkills")
    @Mapping(target = "maxAttendees", source = "maxAttendees")
    @Mapping(target = "type", source = "eventType")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "status", source = "eventStatus", defaultValue = "PLANNED")
    public abstract Event toEntity(EventDto eventDto);

    @Mapping(target = "relatedSkills", source = "relatedSkills")
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "eventType", source = "type")
    @Mapping(target = "eventStatus", source = "status")
    public abstract EventDto toDto(Event event);

    public List<Skill> map(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }

    public List<Long> mapToLong(List<Skill> skills) {
        return skills.stream().map(Skill::getId).collect(Collectors.toList());
    }

    public List<EventDto> toDtoList(List<Event> events) {
        if (events == null) {
            return Collections.emptyList();
        }
        return events.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

