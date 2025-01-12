package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.CreateEventRequestDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventResponseDto;
import school.faang.user_service.dto.event.UpdateEventRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private UserService userService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventParticipationRepository eventParticipationRepository;

    private CreateEventRequestDto createEventRequestDto;
    private UpdateEventRequestDto updateEventRequestDto;
    private EventResponseDto eventResponseDto;
    private Event event;
    private User owner;
    private Skill skill;

    @Captor
    private ArgumentCaptor<Specification<Event>> specCaptor;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setUsername("John");

        skill = new Skill();
        skill.setId(1L);

        event = new Event();
        event.setId(1L);
        event.setOwner(owner);
        event.setTitle("Test Event");

        createEventRequestDto = CreateEventRequestDto.builder()
                .title("Test Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .ownerId(1L)
                .relatedSkills(List.of(1L))
                .build();

        updateEventRequestDto = UpdateEventRequestDto.builder()
                .id(1L)
                .title("Updated Event")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(2))
                .ownerId(1L)
                .relatedSkills(List.of(1L))
                .maxAttendees(100)
                .build();

        eventResponseDto = EventResponseDto.builder()
                .id(1L)
                .title("Test Event")
                .ownerId(1L)
                .relatedSkills(List.of(1L))
                .build();
    }

    @Test
    void shouldCreateEvent() throws DataValidationException {
        when(userService.getUser(anyLong())).thenReturn(owner);
        when(skillRepository.findById(anyLong())).thenReturn(Optional.of(skill));
        when(eventMapper.toEntity(any(CreateEventRequestDto.class))).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDto);
        EventResponseDto result = eventService.createEvent(createEventRequestDto);
        assertNotNull(result);
        assertEquals("Test Event", result.getTitle());
        verify(userService).getUser(createEventRequestDto.getOwnerId());
        verify(skillRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toResponseDto(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenSkillNotFound() {
        when(userService.getUser(anyLong())).thenReturn(owner);
        when(skillRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(eventMapper.toEntity(any(CreateEventRequestDto.class))).thenReturn(event);
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.createEvent(createEventRequestDto)
        );

        assertEquals("Skill not found with ID: 1", exception.getMessage());
        verify(skillRepository).findById(1L);
    }

    @Test
    void shouldGetEventById() throws DataValidationException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDto);
        EventResponseDto result = eventService.getEvent(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Event", result.getTitle());
        verify(eventRepository).findById(1L);
        verify(eventMapper).toResponseDto(event);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> eventService.getEvent(1L)
        );

        assertEquals("Event not found with ID: 1", exception.getMessage());
        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldUpdateEvent() throws DataValidationException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        when(userService.getUser(anyLong())).thenReturn(owner);
        Event updatedEvent = new Event();
        updatedEvent.setId(1L);
        updatedEvent.setOwner(owner);
        updatedEvent.setTitle("Updated Event");
        updatedEvent.setRelatedSkills(List.of(skill));
        when(eventMapper.toEntity(any(UpdateEventRequestDto.class))).thenReturn(updatedEvent);
        when(eventRepository.save(updatedEvent)).thenReturn(updatedEvent);
        when(eventMapper.toResponseDto(updatedEvent)).thenReturn(
                EventResponseDto.builder()
                        .id(1L)
                        .title("Updated Event")
                        .ownerId(owner.getId())
                        .relatedSkills(List.of(1L))
                        .build()
        );
        EventResponseDto result = eventService.updateEvent(updateEventRequestDto);
        assertNotNull(result);
        assertEquals("Updated Event", result.getTitle());
        verify(eventRepository).findById(1L);
        verify(eventRepository).save(updatedEvent);
        verify(eventMapper).toResponseDto(updatedEvent);
    }

    @Test
    void shouldDeleteEvent() throws DataValidationException {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventParticipationRepository.findAllParticipantsByEventId(1L)).thenReturn(List.of());
        eventService.deleteEvent(1L);
        verify(eventRepository).findById(1L);
        verify(eventRepository).delete(event);
    }

    @Test
    void shouldGetEventsByFilters() {
        EventFilterDto filterDto = new EventFilterDto();
        filterDto.setId(1L);

        List<Event> mockedEvents = List.of(event);
        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(mockedEvents);
        when(eventMapper.toResponseDtoList(anyList())).thenAnswer(invocation -> {
            List<Event> events = invocation.getArgument(0);
            return events.stream()
                    .map(e -> EventResponseDto.builder()
                            .id(e.getId())
                            .title(e.getTitle())
                            .ownerId(e.getOwner().getId())
                            .relatedSkills(List.of(1L))
                            .build())
                    .collect(Collectors.toList());
        });
        List<EventResponseDto> result = eventService.getEventsByFilters(filterDto);
        System.out.println("Mocked events: " + mockedEvents.size());
        System.out.println("Resulting list size: " + result.size());
        assertNotNull(result, "Result list should not be null");
        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getTitle());
        verify(eventRepository).findAll(any(Specification.class));
        verify(eventMapper).toResponseDtoList(mockedEvents);
    }
    @Test
    void shouldGetEventsByOwner() {
        when(eventRepository.findAllByUserId(1L)).thenReturn(List.of(event));
        when(eventMapper.toResponseDtoList(any())).thenReturn(List.of(eventResponseDto));
        List<EventResponseDto> result = eventService.getEventsByOwner(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getTitle());
        verify(eventRepository).findAllByUserId(1L);
    }

    @Test
    void shouldGetEventsByParticipant() {
        when(eventRepository.findParticipatedEventsByUserId(1L)).thenReturn(List.of(event));
        when(eventMapper.toResponseDtoList(any())).thenReturn(List.of(eventResponseDto));
        List<EventResponseDto> result = eventService.getEventsByParticipant(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getTitle());
        verify(eventRepository).findParticipatedEventsByUserId(1L);
    }
}