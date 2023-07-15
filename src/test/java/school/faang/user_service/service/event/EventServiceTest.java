package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDto eventDto;
    private User user;

    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));
    }

    private void createUser() {
        user = new User();
        user.setId(1L);
        user.setSkills(createSkills());
    }

    private void createEvent() {
        createUser();
        event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");
        event.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event.setOwner(user);
        event.setRelatedSkills(createSkills());
    }

    private List<Skill> createSkills() {
        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("A");
        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("B");
        return List.of(skill1, skill2);
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setId(i);
            users.add(user);
        }
        return users;
    }

    private List<Event> createEvents() {
        List<Event> events = new ArrayList<>();
        createUser();
        for (int i = 1; i <= 5; i++) {
            Event event = new Event();
            event.setId(i);
            event.setTitle("Test Event " + i);
            event.setStartDate(LocalDate.of(2020, 1, i).atStartOfDay());
            event.setOwner(user);
            event.setAttendees(createUsers());
            event.setRelatedSkills(createSkills());

            events.add(event);
        }
        return events;
    }

    @Test
    public void invalid_EventId() {
        eventDto.setId(0L);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_TitleBlank() {
        eventDto.setTitle("");

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_NullStartDate() {
        eventDto.setStartDate(null);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_NullOwnerId() {
        eventDto.setOwnerId(null);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_userNotContainsSkills() {
        createUser();
        eventDto.setRelatedSkills(List.of(new SkillDto(3L, "C"), new SkillDto(2L, "B")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));

        verify(userRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnEventDto() {
        createEvent();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(ArgumentMatchers.any(Event.class))).thenReturn(event);

        EventDto createdEventDto = eventService.create(eventDto);

        assertEquals(eventDto.getTitle(), createdEventDto.getTitle());
        assertEquals(eventDto.getStartDate(), createdEventDto.getStartDate());
        assertEquals(eventDto.getOwnerId(), createdEventDto.getOwnerId());
        assertThat(eventDto.getRelatedSkills()).containsExactlyInAnyOrderElementsOf(createdEventDto.getRelatedSkills());

        verify(userRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getParticipatedEvents_ShouldReturnEventDtos() {
        long userId = 1L;
        List<Event> participatedEvents = createEvents();

        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(participatedEvents);

        List<EventDto> eventDtos = eventService.getParticipatedEvents(userId);

        assertNotNull(eventDtos);
        assertEquals(participatedEvents.size(), eventDtos.size());
    }
}