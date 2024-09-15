package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @InjectMocks
    EventService eventService;

    @Spy
    @InjectMocks
    private EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    @Spy
    SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private EventFilter eventFilter;

    @Mock
    private UserRepository userRepository;

    private Event event;
    private EventDto eventDto;
    private Event event1;
    private Event event2;

    private EventFilterDto eventFilterDto;



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
                .startDate(LocalDateTime.of(2024, 7, 31, 10, 30))
                .endDate(LocalDateTime.of(2024, 8, 1, 10, 30))
                .location("Event Location")
                .maxAttendees(100)
                .owner(owner)
                .relatedSkills(Collections.singletonList(skill))
                .build();

        eventDto = EventDto.builder()
                .id(1L)
                .title("Event Title")
                .description("Event Description")
                .startDate(LocalDateTime.of(2024, 7, 31, 10, 30))
                .endDate(LocalDateTime.of(2024, 8, 1, 10, 30))
                .location("Event Location")
                .maxAttendees(100)
                .ownerId(1L)
                .relatedSkills(Collections.singletonList(new SkillDto(1L, "Skill1")))
                .build();

        event1 = Event.builder()
                .id(1L)
                .title("Event 1")
                .startDate(LocalDateTime.now())
                .build();

        event2 = Event.builder()
                .id(2L)
                .title("Event 2")
                .startDate(LocalDateTime.now())
                .build();
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventRepository.save(any())).thenReturn(event);

        // Act
        EventDto result = eventService.create(eventDto);

        // Assert
        assertEquals(eventDto, result);
    }

    @Test
    void getEvent_shouldReturnEventDto() {
        // Arrange
        Long eventId = eventDto.id();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDto result = eventService.getEvent(eventId);

        //Assert
        assertEquals(eventDto, result);
    }

    @Test
    void getEventsByFilter_appliesFiltersAndReturnsEventDtos() {
        // Arrange
        List<Event> events = List.of(event);
        List<EventDto> eventDtos = List.of(eventDto);
        EventFilterDto filterDto = EventFilterDto.builder().ownerIdFilter(1L).build();

        when(eventRepository.findAll()).thenReturn(events);
        when(eventMapper.toListDto(anyList())).thenReturn(eventDtos);
        when(eventFilter.isApplicable(filterDto)).thenReturn(true);
        when(eventFilter.apply(any(Stream.class), any(EventFilterDto.class))).thenReturn(events.stream());

        eventService = new EventService(eventRepository, eventMapper, eventValidator, List.of(eventFilter), userRepository);

        // Act
        List<EventDto> result = eventService.getEventsByFilter(filterDto);

        // Assert
        assertEquals(eventDtos, result);
        verify(eventRepository).findAll();
        verify(eventMapper).toListDto(events);
        verify(eventFilter).isApplicable(filterDto);
        verify(eventFilter).apply(any(), eq(filterDto));
    }

    @Test
    void deleteEvent_whenEventExists_shouldCallDeleteById() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(eventRepository).deleteById(eventId);
        verify(eventRepository).existsById(eventId);
    }

    @Test
    void deleteEvent_whenEventDoesNotExist_shouldNotCallDeleteById() {
        // Arrange
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // Act
        eventService.deleteEvent(eventId);

        // Assert
        verify(eventRepository, never()).deleteById(eventId);
        verify(eventRepository).existsById(eventId);
    }

    // TODO:: TEST ERROR FIX
    @Test
    void updateEvent_shouldReturnUpdatedEventDto() {
        // Arrange
        Event savedEvent = event; // Simulating saved entity

        when(eventMapper.toEntity(eventDto)).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(eventDto);

        // Act
        EventDto result = eventService.updateEvent(eventDto);

        // Assert
        assertEquals(eventDto, result);
        verify(eventValidator).validateOwnerSkills(eventDto);
        verify(eventMapper).toEntity(eventDto);
        verify(eventRepository).save(event);
        verify(eventMapper).toDto(savedEvent);
    }

    @Test
    void getOwnedEvents_shouldReturnListOfEvents() {
        // Arrange
        Long userId = 1L;
        List<Event> expectedEvents = List.of(event1, event2);

        when(eventRepository.findAllByUserId(userId)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.getOwnedEvents(userId);

        // Assert
        assertEquals(expectedEvents, result);
        verify(eventRepository).findAllByUserId(userId);
    }

    @Test
    void getParticipatedEvents_shouldReturnListOfEvents() {
        // Arrange
        Long userId = 1L;
        List<Event> expectedEvents = List.of(event1, event2);

        // Stub the repository method
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventService.getParticipatedEvents(userId);

        // Assert
        assertEquals(expectedEvents, result);
        verify(eventRepository).findParticipatedEventsByUserId(userId);
    }
}
