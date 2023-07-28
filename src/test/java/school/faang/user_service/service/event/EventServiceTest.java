package school.faang.user_service.service.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;


    EventDto eventDto;
    Event event;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, userRepository);

        User user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Ability");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Expertise");
        user.setSkills(List.of(skill1, skill2));

        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("My Event");
        eventDto.setStartDate(LocalDate.of(2023, 7, 27).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "Ability"), new SkillDto(2L, "Expertise")));

        event = new Event();
        event.setId(1L);
    }

    @Test
    void testCreate_EventExists() {
        List<Event> existingEvents = new ArrayList<>();
        existingEvents.add(event);

        assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
    }

    @Test
    void testCreate_EventDoesNotExistReturnDto() {
        User user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Ability");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Expertise");
        user.setSkills(List.of(skill1, skill2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        eventService = new EventService(eventRepository, userRepository);
        when(eventRepository.findByEventId(event.getId())).thenReturn(new ArrayList<>());
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto.getId(), result.getId());
    }

    @Test
    void testCreate_ValidOwnerId() {
        User user = new User();
        user.setId(1L);

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
}