package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
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

    private Iterable<Event> createEventIterable() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Title");
        Event event2 = new Event();
        event2.setStartDate(eventDto.getStartDate());
        event2.setEndDate(LocalDate.of(2021, 1, 1).atStartOfDay());
        Event event3 = new Event();
        event3.setMaxAttendees(5);
        return List.of(event1, event2, event3);
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
    void getEventsByFilter_IdTitleFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setId(1L);
        filter.setTitle("Title");

        when(eventRepository.findAll()).thenReturn(createEventIterable());

        List<EventDto> actual = eventService.getEventsByFilter(filter);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(1, actual.get(0).getId());
    }

    @Test
    void getEventsByFilter_DateFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLaterThanStartDate(LocalDate.of(2020, 6, 1).atStartOfDay());
        filter.setEarlierThanEndDate(LocalDate.of(2020, 7, 20).atStartOfDay());

        when(eventRepository.findAll()).thenReturn(createEventIterable());

        List<EventDto> actual = eventService.getEventsByFilter(filter);

        assertNotNull(actual);
        assertEquals(1, actual.size());
    }

    @Test
    void getEventsByFilter_maxAttendeesFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLessThanMaxAttendees(2);

        when(eventRepository.findAll()).thenReturn(createEventIterable());

        List<EventDto> actual = eventService.getEventsByFilter(filter);

        assertNotNull(actual);
        assertEquals(2, actual.size());
    }
}