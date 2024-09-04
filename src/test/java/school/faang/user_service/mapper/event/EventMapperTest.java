package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventMapper = Mappers.getMapper(EventMapper.class);
    }

    @Test
    void testEventToEventDtoMapping() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Java Conference");
        event.setDescription("A conference about Java");
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusDays(1));
        event.setLocation("New York");
        event.setMaxAttendees(100);
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill));

        EventDto eventDto = eventMapper.toDto(event);

        assertNotNull(eventDto);
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getTitle(), eventDto.getTitle());
        assertEquals(event.getDescription(), eventDto.getDescription());
        assertEquals(event.getStartDate(), eventDto.getStartDate());
        assertEquals(event.getEndDate(), eventDto.getEndDate());
        assertEquals(event.getLocation(), eventDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventDto.getMaxAttendees());
        assertEquals(event.getOwner().getId(), eventDto.getOwnerId());
        assertEquals(1, eventDto.getRelatedSkillsIds().size());
        assertEquals(1L, eventDto.getRelatedSkillsIds().get(0));
    }

    @Test
    void testEventDtoToEventMapping() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Java Conference");
        eventDto.setDescription("A conference about Java");
        eventDto.setStartDate(LocalDateTime.now());
        eventDto.setEndDate(LocalDateTime.now().plusDays(1));
        eventDto.setLocation("New York");
        eventDto.setMaxAttendees(100);
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkillsIds(List.of(1L));

        Event event = eventMapper.toEvent(eventDto);

        assertNotNull(event);
        assertEquals(eventDto.getId(), event.getId());
        assertEquals(eventDto.getTitle(), event.getTitle());
        assertEquals(eventDto.getDescription(), event.getDescription());
        assertEquals(eventDto.getStartDate(), event.getStartDate());
        assertEquals(eventDto.getEndDate(), event.getEndDate());
        assertEquals(eventDto.getLocation(), event.getLocation());
        assertEquals(eventDto.getMaxAttendees(), event.getMaxAttendees());
    }

    @Test
    void testEventToEventWithSubscribersDtoMapping() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Java Conference");
        event.setDescription("A conference about Java");
        event.setStartDate(LocalDateTime.now());
        event.setEndDate(LocalDateTime.now().plusDays(1));
        event.setLocation("New York");
        event.setMaxAttendees(100);
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill));

        EventWithSubscribersDto eventWithSubscribersDto = eventMapper.toEventWithSubscribersDto(event);

        assertNotNull(eventWithSubscribersDto);
        assertEquals(event.getId(), eventWithSubscribersDto.getId());
        assertEquals(event.getTitle(), eventWithSubscribersDto.getTitle());
        assertEquals(event.getDescription(), eventWithSubscribersDto.getDescription());
        assertEquals(event.getStartDate(), eventWithSubscribersDto.getStartDate());
        assertEquals(event.getEndDate(), eventWithSubscribersDto.getEndDate());
        assertEquals(event.getLocation(), eventWithSubscribersDto.getLocation());
        assertEquals(event.getMaxAttendees(), eventWithSubscribersDto.getMaxAttendees());
        assertEquals(event.getOwner().getId(), eventWithSubscribersDto.getOwnerId());
        assertEquals(1, eventWithSubscribersDto.getRelatedSkillsIds().size());
        assertEquals(1L, eventWithSubscribersDto.getRelatedSkillsIds().get(0));
    }

    @Test
    void testEventListToEventDtoListMapping() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test_user");

        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");

        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Java Conference");
        event1.setOwner(user);
        event1.setRelatedSkills(List.of(skill));

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Spring Boot Workshop");
        event2.setOwner(user);
        event2.setRelatedSkills(List.of(skill));

        List<EventDto> eventDtos = eventMapper.toDto(List.of(event1, event2));

        assertNotNull(eventDtos);
        assertEquals(2, eventDtos.size());

        assertEquals(event1.getId(), eventDtos.get(0).getId());
        assertEquals(event1.getTitle(), eventDtos.get(0).getTitle());
        assertEquals(event2.getId(), eventDtos.get(1).getId());
        assertEquals(event2.getTitle(), eventDtos.get(1).getTitle());
    }
}