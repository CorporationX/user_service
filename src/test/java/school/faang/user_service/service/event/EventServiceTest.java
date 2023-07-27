package school.faang.user_service.service.event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validate.event.EventValidate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Spy
    private EventMapper eventMapper;

    @Spy
    private UserRepository userRepository;
    @Spy
    private EventRepository eventRepository;

    private EventValidate eventValidate;
    private EventService eventService;

    private Event event;
    private EventDto eventDto;

    @BeforeEach
    void setUp() {
        eventValidate = new EventValidate(userRepository);
        eventService = new EventService(eventRepository, eventMapper, eventValidate);

        Skill skill1 = Skill.builder().id(1L).build();
        Skill skill2 = Skill.builder().id(2L).build();
        Skill skill3 = Skill.builder().id(3L).build();

        User owner = new User();
        owner.setId(1L);

        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .owner(owner)
                .description("This is a test event")
                .relatedSkills(Arrays.asList(skill1, skill2, skill3))
                .location("Test Location")
                .maxAttendees(50)
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .ownerId(1L)
                .description("This is a test event")
                .skillIds(Arrays.asList(1L, 2L, 3L))
                .location("Test Location")
                .maxAttendees(50)
                .build();
    }

    @Test
    void create_ValidEventDto_ReturnsCreatedEventDto() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventMapper.toDto(event)).thenReturn(eventDto);

        // Act
        EventDto createdEventDto = eventService.create(eventDto);

        // Assert
        assertEquals(eventDto.getId(), createdEventDto.getId());
        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
        assertEquals(eventDto.getStartDate(), createdEventDto.getStartDate());
        assertEquals(eventDto.getEndDate(), createdEventDto.getEndDate());
        assertEquals(eventDto.getOwnerId(), createdEventDto.getOwnerId());
        assertEquals(eventDto.getDescription(), createdEventDto.getDescription());
        assertEquals(eventDto.getLocation(), createdEventDto.getLocation());
        assertEquals(eventDto.getMaxAttendees(), createdEventDto.getMaxAttendees());
        assertEquals(eventDto.getSkillIds(), createdEventDto.getSkillIds());
    }
}