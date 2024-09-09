package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidator eventValidator;

    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        Skill skill = Skill.builder()
                .id(1L)
                .title("Skill1")
                .build();

        event = Event.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("Event Location")
                .maxAttendees(100)
                .owner(owner)
                .relatedSkills(Collections.singletonList(skill))
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .location("Event Location")
                .maxAttendees(100)
                .ownerId(1L)
                .relatedSkills(Collections.singletonList(new SkillDto(1L, "Skill1")))
                .build();
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        // Act
        EventDto result = eventService.create(eventDto);

        // Assert
        assertEquals(eventDto, result);
    }

}
