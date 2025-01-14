package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.*;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.adapter.EventRepositoryAdapter;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepositoryAdapter eventRepositoryAdapter;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserService userService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_success() throws DataValidationException {
        CreateEventRequestDto createRequest = CreateEventRequestDto.builder()
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .ownerId(1L)
                .relatedSkills(List.of(1L))
                .description("Test Description")
                .eventType(EventType.MEETING)
                .eventStatus(EventStatus.PLANNED)
                .build();

        Skill skill = new Skill();
        skill.setId(1L);

        User user = new User();
        user.setId(1L);

        Event event = new Event();
        event.setTitle("Test Event");

        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(userService.getUser(1L)).thenReturn(user);
        when(eventMapper.toEntity(any(CreateEventRequestDto.class), anyList())).thenReturn(event);
        when(eventRepositoryAdapter.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(EventResponseDto.builder().id(1L).build());

        EventResponseDto result = eventService.createEvent(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(skillRepository, times(1)).findById(1L);
        verify(userService, times(1)).getUser(1L);
        verify(eventMapper, times(1)).toEntity(createRequest, List.of(skill));
        verify(eventRepositoryAdapter, times(1)).save(eventCaptor.capture());
        assertEquals("Test Event", eventCaptor.getValue().getTitle());
    }

    @Test
    void createEvent_skillNotFound() {
        CreateEventRequestDto createRequest = CreateEventRequestDto.builder()
                .title("Test Event")
                .relatedSkills(List.of(999L))
                .ownerId(1L)
                .build();

        when(skillRepository.findById(999L)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.createEvent(createRequest)
        );

        assertEquals("Skill not found with ID: 999", exception.getMessage());
        verify(skillRepository, times(1)).findById(999L);
        verifyNoInteractions(userService, eventRepositoryAdapter, eventMapper);
    }

    @Test
    void getEvent_success() throws DataValidationException {
        Long eventId = 1L;
        Event event = new Event();

        when(eventRepositoryAdapter.getEventById(eventId)).thenReturn(event);
        EventResponseDto responseDto = EventResponseDto.builder().id(1L).build();
        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(responseDto);

        EventResponseDto result = eventService.getEvent(eventId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepositoryAdapter, times(1)).getEventById(eventId);
        verify(eventMapper, times(1)).toResponseDto(event);
    }

    @Test
    void getEvent_eventNotFound() {
        Long eventId = 999L;
        when(eventRepositoryAdapter.getEventById(eventId)).thenThrow(
                new DataValidationException("Event not found with ID: " + eventId)
        );

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(eventId)
        );

        assertEquals("Event not found with ID: 999", exception.getMessage());
        verify(eventRepositoryAdapter, times(1)).getEventById(eventId);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void deleteEvent_success() throws DataValidationException {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        User user = new User();
        user.setId(2L);

        when(eventRepositoryAdapter.getEventById(eventId)).thenReturn(event);
        when(eventParticipationRepository.findAllParticipantsByEventId(eventId)).thenReturn(List.of(user));

        eventService.deleteEvent(eventId);

        verify(eventParticipationRepository, times(1)).findAllParticipantsByEventId(eventId);
        verify(eventParticipationRepository, times(1)).unregister(eventId, 2L);
        verify(eventRepositoryAdapter, times(1)).delete(event);
    }

    @Test
    void getEventsByFilters_success() {
        EventFilterDto filterDto = new EventFilterDto();
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test Event");

        List<Event> events = List.of(event);
        EventResponseDto responseDto = EventResponseDto.builder()
                .id(1L)
                .title("Test Event")
                .build();

        ArgumentCaptor<Specification<Event>> specCaptor = ArgumentCaptor.forClass(Specification.class);

        when(eventRepositoryAdapter.findAll(any(Specification.class))).thenReturn(events);
        when(eventMapper.toResponseDtoList(anyList())).thenReturn(List.of(responseDto));

        List<EventResponseDto> result = eventService.getEventsByFilters(filterDto);

        verify(eventRepositoryAdapter).findAll(specCaptor.capture());
        Specification<Event> capturedSpec = specCaptor.getValue();
        System.out.println("Captured Specification: " + capturedSpec);

        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should be 1");
        assertEquals("Test Event", result.get(0).getTitle(), "Event title should match");
    }

    @Test
    void updateEvent_success() throws DataValidationException {
        UpdateEventRequestDto updateRequest = UpdateEventRequestDto.builder()
                .id(1L)
                .ownerId(1L)
                .relatedSkills(List.of(1L))
                .title("Updated Event")
                .build();

        Skill skill = new Skill();
        skill.setId(1L);

        Event existingEvent = new Event();
        Event updatedEvent = new Event();

        when(eventRepositoryAdapter.getEventById(1L)).thenReturn(existingEvent);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(eventMapper.toEntity(updateRequest, List.of(skill))).thenReturn(updatedEvent);
        when(eventRepositoryAdapter.save(updatedEvent)).thenReturn(updatedEvent);
        when(eventMapper.toResponseDto(updatedEvent)).thenReturn(EventResponseDto.builder().id(1L).build());

        EventResponseDto result = eventService.updateEvent(updateRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepositoryAdapter, times(1)).getEventById(1L);
        verify(skillRepository, times(1)).findById(1L);
        verify(eventRepositoryAdapter, times(1)).save(updatedEvent);
        verify(eventMapper, times(1)).toResponseDto(updatedEvent);
    }
}