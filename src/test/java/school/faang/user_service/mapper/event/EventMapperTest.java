package school.faang.user_service.mapper.event;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    private final EventMapper eventMapper = EventMapper.INSTANCE;

    private Event event;
    private EventDto eventDto;
    private User user;


    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Ability");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Expertise");
        user.setSkills(List.of(skill1, skill2));

        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .ownerId(1L)
                .relatedSkills(List.of(new SkillDto(1L, "Ability"), new SkillDto(2L, "Expertise")))
                .build();

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .owner(user)
                .relatedSkills(List.of(skill1, skill2))
                .build();
    }

    @Test
    @DisplayName("Dto transform to Entity. Put in database")
    void testToEvent_ReturnsEventEntity() {
        Event result = eventMapper.toEvent(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
        assertEquals(eventDto.getTitle(), result.getTitle());
        assertEquals(eventDto.getStartDate(), result.getStartDate());
        assertEquals(event.getRelatedSkills(), result.getRelatedSkills());
        assertEquals(user.getId(), result.getOwner().getId());
    }

    @Test
    void testToDto_ReturnsEventDto() {
        EventDto result = eventMapper.toDto(event);

        assertNotNull(result);
        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getStartDate(), result.getStartDate());
        assertEquals(eventDto.getRelatedSkills(), result.getRelatedSkills());
        assertEquals(user.getId(), result.getOwnerId());
    }
}