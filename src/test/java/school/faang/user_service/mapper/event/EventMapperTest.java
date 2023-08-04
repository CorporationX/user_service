package school.faang.user_service.mapper.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EventMapperTest {
    @Spy
    private EventMapperImpl eventMapper;
    private Event event;
    private EventDto eventDto;
    private EventDto updatedDto;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .id(1L)
                .build();

        Skill skill1 = Skill.builder()
                .id(1L)
                .title("Ability")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .title("Expertise").build();

        event = Event.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 0, 0))
                .endDate(null)
                .description("Event description")
                .location("Event location")
                .maxAttendees(100)
                .owner(user)
                .relatedSkills(List.of(skill1, skill2))
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 0, 0))
                .endDate(null)
                .description("Event description")
                .location("Event location")
                .maxAttendees(100)
                .ownerId(1L)
                .relatedSkills(List.of(
                        SkillDto.builder().id(1L).title("Ability").build(),
                        SkillDto.builder().id(2L).title("Expertise").build()
                ))
                .build();

        updatedDto = EventDto.builder()
                .id(1L)
                .title("My SuperEvent")
                .maxAttendees(50)
                .ownerId(1L)
                .build();
    }

    @Test
    @DisplayName("Test Event to EventDto Mapping")
    void test_EventToEventDto() {
        EventDto mappedEventDto = eventMapper.toDto(event);
        assertEquals(eventDto, mappedEventDto);
    }

    @Test
    @DisplayName("Test EventDto to Event Mapping")
    void test_EventDtoToEvent() {
        Event mappedEvent = eventMapper.toEvent(eventDto);
        assertEquals(event, mappedEvent);
    }

    @Test
    @DisplayName("Test Event Update")
    void testUpdateDto() {
        eventMapper.updateDto(updatedDto, event);

        assertEquals(updatedDto.getId(), event.getId());
        assertEquals(updatedDto.getTitle(), event.getTitle());
        assertEquals(updatedDto.getMaxAttendees(), event.getMaxAttendees());
        assertEquals(updatedDto.getOwnerId(), event.getOwner().getId());
    }
}