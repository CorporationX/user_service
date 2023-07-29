package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.event.Event;

import java.util.List;

import static java.time.LocalDateTime.now;

public class EventMock {
    public static EventDto getEventDto() {
        EventDto eventDto = new EventDto();

        eventDto.setId(1L);
        eventDto.setTitle("Hiring");
        eventDto.setStartDate(now());
        eventDto.setEndDate(now());
        eventDto.setLocation("Usa");
        eventDto.setDescription("Hiring event");
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(1L));

        return eventDto;
    }

    public static Event getEventEntity() {
        Event event = new Event();
        Skill skill = new Skill();

        event.setId(1L);
        event.setTitle("Hiring");
        event.setStartDate(now());
        event.setEndDate(now());
        event.setLocation("Usa");
        event.setDescription("Hiring event");

        event.setRelatedSkills(List.of(skill));

        return event;
    }
}
