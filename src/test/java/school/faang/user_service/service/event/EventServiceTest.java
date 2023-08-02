package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.event.EventMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    @Spy
    private EventMapperImpl eventMapper;
    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    EventDto eventDto;
    Event event;
    User user;
    Skill skill;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Ability");

        user.setSkills(List.of(skill));

        eventDto = EventDto.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 10, 0))
                .endDate(LocalDateTime.of(2023, 7, 27, 15, 0))
                .ownerId(1L)
                .relatedSkills(List.of(SkillDto.builder().id(1L).title("Ability").build()))
                .location("Conference Hall")
                .maxAttendees(100)
                .build();

        event = Event.builder()
                .id(1L)
                .title("My Event")
                .startDate(LocalDateTime.of(2023, 7, 27, 10, 0))
                .endDate(LocalDateTime.of(2023, 7, 27, 15, 0))
                .owner(user)
                .relatedSkills(List.of(skill))
                .location("Conference Hall")
                .maxAttendees(100)
                .build();
    }

    @Test
    void testCreate_EventExists() {
        List<Event> existingEvents = new ArrayList<>();
        existingEvents.add(event);

        when(eventRepository.findByEventId(event.getId())).thenReturn(existingEvents);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_EventDoesNotExist_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findByEventId(event.getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_ValidOwnerId() {
        eventDto.setOwnerId(1L);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_IfOwnerIdNull() {
        eventDto.setOwnerId(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_IfOwnerIdNegative() {
        eventDto.setOwnerId(-1L);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_ValidEventTitle() {
        eventDto.setTitle("My Title");

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_NullEventTitle() {
        eventDto.setTitle(null);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_BlankEventTitle() {
        eventDto.setTitle("     ");

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_UserHasNecessarySkills_ReturnsDto() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));
        when(eventMapper.toEvent(eventDto)).thenReturn(event);
        when(eventRepository.findByEventId(event.getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_UserDoesNotHaveNecessarySkills() {
        eventDto.setRelatedSkills(List.of(SkillDto.builder().id(2L).title("Expertise").build()));

        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.of(user));

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }
}