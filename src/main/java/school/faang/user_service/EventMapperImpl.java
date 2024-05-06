package school.faang.user_service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public EventDto toDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setTitle(event.getTitle());
        eventDto.setOwnerId(event.getOwner().getId());
        eventDto.setRelatedSkills(event.getRelatedSkills().stream()
                .map(skill -> toSkillDto(skill)).collect(Collectors.toList()));

        return eventDto;
    }

    @Override
    public Event toEntity(EventDto eventDto) {
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setTitle(eventDto.getTitle());
        event.setRelatedSkills(eventDto.getRelatedSkills().stream()
                .map(skillDto -> toSkill(skillDto)).collect(Collectors.toList()));

        return event;
    }

    @Override
    public List<EventDto> toEventsDto(List<Event> events) {
        return events.stream().map(event -> toDto(event)).collect(Collectors.toList());
    }

    @Override
    public List<Event> toEvents(List<EventDto> eventsDto) {
        return eventsDto.stream().map(eventDto -> toEntity(eventDto)).collect(Collectors.toList());
    }

    private Skill toSkill(SkillDto skillDto) {
        Skill skill = new Skill();
        skill.setId(skillDto.getId());
        skill.setTitle(skillDto.getTitle());

        return skill;
    }

    private SkillDto toSkillDto(Skill skill) {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(skill.getId());
        skillDto.setTitle(skill.getTitle());

        return skillDto;
    }
}