package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventDateFilter;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.filter.event.EventIdFilter;
import school.faang.user_service.filter.event.EventMaxAttendeesFilter;
import school.faang.user_service.filter.event.EventTitleFilter;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    private EventService eventService;

    private Event event;
    private User user;

    @BeforeEach
    void setUp() {
        List<EventFilter> eventFilters = List.of(
                new EventIdFilter(),
                new EventTitleFilter(),
                new EventDateFilter(),
                new EventMaxAttendeesFilter()
        );
        eventService = new EventService(eventRepository, userRepository, eventFilters);
    }

    private EventDto createEventDto() {
        EventDto eventDto = new EventDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");
        eventDto.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(List.of(new SkillDto(1L, "A"), new SkillDto(2L, "B")));
        return eventDto;
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


    @Test
    public void invalid_EventId() {
        EventDto eventDto = createEventDto();
        eventDto.setId(0L);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_TitleBlank() {
        EventDto eventDto = createEventDto();
        eventDto.setTitle("");

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_NullStartDate() {
        EventDto eventDto = createEventDto();
        eventDto.setStartDate(null);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_NullOwnerId() {
        EventDto eventDto = createEventDto();
        eventDto.setOwnerId(null);

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));
    }

    @Test
    public void invalid_userNotContainsSkills() {
        EventDto eventDto = createEventDto();
        createUser();
        eventDto.setRelatedSkills(List.of(new SkillDto(3L, "C"), new SkillDto(2L, "B")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> eventService.create(eventDto));

        verify(userRepository).findById(1L);
    }

    @Test
    void create_ShouldReturnEventDto() {
        EventDto eventDto = createEventDto();
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
        filter.setTitlePattern("^[a-zA-Z]+$");

        when(eventRepository.findAll()).thenReturn(createEventDtoList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    void getEventsByFilter_DateFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLaterThanStartDate(LocalDate.of(2019, 6, 1).atStartOfDay());
        filter.setEarlierThanEndDate(LocalDate.of(2022, 7, 20).atStartOfDay());

        when(eventRepository.findAll()).thenReturn(createEventDtoList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    void getEventsByFilter_maxAttendeesFilter() {
        EventFilterDto filter = new EventFilterDto();
        filter.setLessThanMaxAttendees(2);

        when(eventRepository.findAll()).thenReturn(createEventDtoList());

        List<EventDto> result = eventService.getEventsByFilter(filter);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotEquals(3L, result.get(0).getId());
        assertNotEquals(3L, result.get(1).getId());
    }

    private List<Event> createEventDtoList() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Title");
        Event event2 = new Event();
        event2.setId(2L);
        event2.setStartDate(LocalDate.of(2020, 1, 1).atStartOfDay());
        event2.setEndDate(LocalDate.of(2021, 1, 1).atStartOfDay());
        Event event3 = new Event();
        event3.setId(3L);
        event3.setMaxAttendees(5);
        return List.of(event1, event2, event3);
    }
}