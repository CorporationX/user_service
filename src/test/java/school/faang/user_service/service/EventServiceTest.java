package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.EventFilter;
import school.faang.user_service.filter.EventLocationFilter;
import school.faang.user_service.mapper.EventMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    private static final LocalDateTime START_DATE =
            LocalDateTime.of(2025, 5, 30, 15, 0);
    private static final LocalDateTime END_DATE =
            LocalDateTime.of(2025, 5, 31, 15, 0);
    private static final String DATA_EXCEPTION_MESSAGE = "Некорректно введены даты события";
    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден";
    private static final Long USER_ID = 1L;
    private static final Long EVENT_ID = 1L;

    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private EventMapperImpl eventMapper;
    @Mock
    private List<EventFilter> eventFilters;

    @InjectMocks
    private EventService eventService;

    private EventDto eventDto;
    private static User user;
    private static Skill firstSkill;
    private static Skill secondSkill;

    @BeforeEach
    public void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("test event");
        eventDto.setStartDate(START_DATE);
        eventDto.setEndDate(END_DATE);
        eventDto.setOwnerId(USER_ID);
        eventDto.setRelatedSkills(List.of(1L, 2L));
    }

    @BeforeAll
    public static void init() {
        firstSkill = new Skill();
        secondSkill = new Skill();
        firstSkill.setId(1L);
        secondSkill.setId(2L);
        user = new User();
        user.setId(USER_ID);
        user.setUsername("testUser");
        user.setSkills(List.of(firstSkill, secondSkill));
    }

    @Test
    public void testCreateWithNullStartDate() {
        eventDto.setStartDate(null);

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals(DATA_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void testCreateWithNullEndDate() {
        eventDto.setEndDate(null);

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals(DATA_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void testCreateWithStartDateAfterEndDate() {
        eventDto.setStartDate(END_DATE);
        eventDto.setEndDate(START_DATE);

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals(DATA_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    public void testCreateWithoutOwner() {
        when(userRepository.findById(eventDto.getOwnerId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    public void testCreateWithNoRelatedSkills() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.create(eventDto));
        assertEquals("Попытка создания события пользователем без требуемых навыков", exception.getMessage());
    }

    @Test
    public void testCreateSuccessfully() {
        List<Skill> relatedSkills = List.of(firstSkill, secondSkill);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(relatedSkills);
        Event event = eventMapper.toEntity(eventDto, user, relatedSkills);
        eventDto.setId(0L);
        when(eventRepository.save(event)).thenReturn(event);

        EventDto result = eventService.create(eventDto);

        assertNotNull(result);
        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testGetEventWithWrongId() {
        when(eventRepository.findById(3L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.getEvent(3L));
        assertEquals("Неверно указан ID пользователя", exception.getMessage());
    }

    @Test
    public void testGetEventWithCorrectId() {
        EventDto expected = new EventDto();
        expected.setId(EVENT_ID);
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));

        EventDto result = eventService.getEvent(EVENT_ID);

        assertNotNull(result);
        assertEquals(expected, result);
    }

    @Test
    public void testGetEventsByFilterSuccessfully() {
        EventFilterDto eventFilterDto = new EventFilterDto();
        eventFilterDto.setLocation("Izhevsk");
        Event firstEvent = new Event();
        Event secondEvent = new Event();
        firstEvent.setId(EVENT_ID);
        secondEvent.setId(EVENT_ID + 1);
        firstEvent.setTitle("first event");
        secondEvent.setTitle("second event");
        firstEvent.setLocation("Moscow");
        secondEvent.setLocation("Izhevsk");
        when(eventRepository.findAll()).thenReturn(List.of(firstEvent, secondEvent));
        EventFilter eventLocationFilter = new EventLocationFilter();
        when(eventFilters.iterator()).thenReturn(List.of(eventLocationFilter).iterator());

        List<EventDto> result = eventService.getEventsByFilter(eventFilterDto);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Izhevsk", result.get(0).getLocation());
    }

    @Test
    public void testDeleteEventSuccessfully() {
        eventService.deleteEvent(EVENT_ID);
        verify(eventRepository, times(1)).deleteById(EVENT_ID);
    }

    @Test
    public void testUpdateWithEventDtoIdIsNull() {
        Exception exception = assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        assertEquals("ID не может быть null, чтобы обновить событие", exception.getMessage());
    }

    @Test
    public void testUpdateWithWrongEventDtoId() {
        eventDto.setId(1L);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        assertEquals("Передан неверный ID события", exception.getMessage());
    }

    @Test
    public void testUpdateWithWrongOwnerId() {
        eventDto.setId(EVENT_ID);
        eventDto.setOwnerId(USER_ID);
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    }

    @Test
    public void testUpdateWithoutRelatedSkills() {
        eventDto.setId(EVENT_ID);
        eventDto.setOwnerId(USER_ID);
        Event event = new Event();
        event.setId(1L);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(DataValidationException.class, () -> eventService.updateEvent(eventDto));
        assertEquals("Попытка обновления события пользователем без требуемых навыков", exception.getMessage());
    }

    @Test
    public void testUpdateSuccessfully() {
        eventDto.setId(EVENT_ID);
        eventDto.setOwnerId(USER_ID);
        Event event = new Event();
        event.setId(EVENT_ID);
        when(eventRepository.findById(EVENT_ID)).thenReturn(Optional.of(event));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        when(skillRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstSkill, secondSkill));
        when(eventRepository.save(event)).thenReturn(event);

        EventDto result = eventService.updateEvent(eventDto);

        assertNotNull(result);
        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    public void testGetOwnedEventsSuccessfully() {
        Event firstEvent = new Event();
        Event secondEvent = new Event();
        firstEvent.setId(EVENT_ID);
        secondEvent.setId(EVENT_ID + 1);
        firstEvent.setOwner(user);
        secondEvent.setOwner(user);
        when(eventRepository.findAllByUserId(USER_ID)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> result = eventService.getOwnedEvents(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    public void testGetParticipatedEventsSuccessfully() {
        Event firstEvent = new Event();
        Event secondEvent = new Event();
        firstEvent.setId(EVENT_ID);
        secondEvent.setId(EVENT_ID + 1);
        firstEvent.setAttendees(List.of(user));
        secondEvent.setAttendees(List.of(user));
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(List.of(firstEvent, secondEvent));

        List<EventDto> result = eventService.getParticipatedEvents(user.getId());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(USER_ID);
    }
}
