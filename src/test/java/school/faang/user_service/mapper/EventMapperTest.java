package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventMapperTest {
    private final EventMapper eventMapper = EventMapper.INSTANCE;

    private EventDto eventDto;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("A");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("B");
        user.setSkills(List.of(skill1, skill2));

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));

        event = new Event();
        event.setId(1);
        event.setTitle("Title");
        event.setStartDate(LocalDate.of(2020, 1, 18).atStartOfDay());
        event.setOwner(user);
        event.setRelatedSkills(List.of(skill1, skill2));
    }

    @Test
    @DisplayName("Test mapping EventDto to Event")
    void toEntityTest() {
        Event mappedEvent = eventMapper.toEntity(eventDto);

        assertNotNull(mappedEvent);
        assertEquals(eventDto.getId(), mappedEvent.getId());
        assertEquals(eventDto.getTitle(), mappedEvent.getTitle());
        assertEquals(eventDto.getStartDate(), mappedEvent.getStartDate());
        assertEquals(event.getRelatedSkills(), mappedEvent.getRelatedSkills());
        assertEquals(user.getId(), mappedEvent.getOwner().getId());
    }

    @Test
    @DisplayName("Test mapping Event to EventDto")
    void toEventDtoTest() {
        EventDto mapppedEventDto = eventMapper.toDto(event);

        assertNotNull(mapppedEventDto);
        assertEquals(event.getId(), mapppedEventDto.getId());
        assertEquals(event.getTitle(), mapppedEventDto.getTitle());
        assertEquals(event.getStartDate(), mapppedEventDto.getStartDate());
        assertEquals(user.getId(), mapppedEventDto.getOwnerId());
        assertEquals(eventDto.getRelatedSkills(), mapppedEventDto.getRelatedSkills());
    }
}