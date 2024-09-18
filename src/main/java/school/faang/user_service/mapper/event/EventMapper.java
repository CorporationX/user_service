package school.faang.user_service.mapper.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.SkillMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final SkillMapper skillMapper;

    public EventDto toDto(Event event) {
        Long ownerId = event.getOwner().getId();

        List<SkillDto> skillDtoList = event.getRelatedSkills()
                .stream()
                .map(skillMapper::toDto)
                .toList();

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .location(event.getLocation())
                .maxAttendees(event.getMaxAttendees())
                .ownerId(ownerId)
                .relatedSkills(skillDtoList)
                .type(event.getType())
                .status(event.getStatus())
                .build();
    }

    public Event toEntity(EventDto eventDto) {
        Event.EventBuilder event = Event.builder();

        List<Skill> skillDtoList = eventDto.getRelatedSkills()
                .stream()
                .map(skillMapper::toEntity)
                .toList();

        if (eventDto.getId() != null) {
            event.id(eventDto.getId());
        }
        event.title(eventDto.getTitle());
        event.description(eventDto.getDescription());
        event.startDate(eventDto.getStartDate());
        event.endDate(eventDto.getEndDate());
        event.location(eventDto.getLocation());
        event.maxAttendees(eventDto.getMaxAttendees());
        event.owner(getOwner(eventDto));
        event.relatedSkills(skillDtoList);
        event.type(eventDto.getType());
        event.status(eventDto.getStatus());

        return event.build();
    }

    private User getOwner(EventDto eventDto) {
        User user = new User();
        user.setId(eventDto.getOwnerId());
        return user;
    }
}
