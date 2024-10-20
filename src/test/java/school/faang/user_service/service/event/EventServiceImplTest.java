package school.faang.user_service.service.event;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.model.dto.event.EventDto;
import school.faang.user_service.model.dto.event.EventFilterDto;
import school.faang.user_service.model.dto.skill.SkillDto;
import school.faang.user_service.model.entity.Skill;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.event.Event;
import school.faang.user_service.model.event.EventStartEvent;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.impl.event.EventServiceImpl;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @InjectMocks
    EventServiceImpl eventServiceImpl;

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

    @Mock
    private EventStartEventPublisher publisher;

    @Captor
    private ArgumentCaptor<EventStartEvent> eventStartCaptor;

    private Event event;
    private EventDto eventDto;
    private Event event1;
    private Event event2;

    private EventFilterDto eventFilterDto;
    private List<Event> events;


    @BeforeEach
    void setUp() {
        User owner = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        List<User> attendees = List.of(user2, user1);

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
                .attendees(attendees)
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

        ReflectionTestUtils.setField(eventServiceImpl, "batchSize", 3);
    }

    @Test
    void create_shouldReturnEventDto() {
        // Arrange
        when(eventRepository.save(any())).thenReturn(event);

        // Act
        EventDto result = eventServiceImpl.create(eventDto);

        // Assert
        assertEquals(eventDto, result);
    }

    @Test
    void getEvent_shouldReturnEventDto() {
        // Arrange
        Long eventId = eventDto.id();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDto result = eventServiceImpl.getEvent(eventId);

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

        eventServiceImpl = new EventServiceImpl(eventRepository, eventMapper, eventValidator, List.of(eventFilter), publisher);

        // Act
        List<EventDto> result = eventServiceImpl.getEventsByFilter(filterDto);

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
        eventServiceImpl.deleteEvent(eventId);

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
        eventServiceImpl.deleteEvent(eventId);

        // Assert
        verify(eventRepository, never()).deleteById(eventId);
        verify(eventRepository).existsById(eventId);
    }

    @Test
    void updateEvent_shouldReturnUpdatedEventDto() {
        // Arrange
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Act
        EventDto result = eventServiceImpl.updateEvent(eventDto);

        // Assert
        assertEquals(eventDto, result);
    }

    @Test
    void getOwnedEvents_shouldReturnListOfEvents() {
        // Arrange
        Long userId = 1L;
        List<Event> expectedEvents = List.of(event1, event2);

        when(eventRepository.findAllByUserId(userId)).thenReturn(expectedEvents);

        // Act
        List<Event> result = eventServiceImpl.getOwnedEvents(userId);

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
        List<Event> result = eventServiceImpl.getParticipatedEvents(userId);

        // Assert
        assertEquals(expectedEvents, result);
        verify(eventRepository).findParticipatedEventsByUserId(userId);
    }

    @Test
    void deletePassedEvents_shouldPartitionAndDeleteInBatches() {
        // Arrange
        events = getEventList();
        when(eventRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(events);
        // Act
        eventServiceImpl.deletePassedEvents();
        // Assert
        verify(eventRepository, times(3)).deleteAllInBatch(any());
    }

    @Test
    void testFindEventsStartingRightNow(){
        when(eventRepository.findAllByStartDate(any())).thenReturn(List.of(event));

        eventServiceImpl.findEventsStartingRightNow();

        verify(publisher).publish(eventStartCaptor.capture());

        assertEquals(1L, eventStartCaptor.getValue().id());
        assertEquals(List.of(2L, 1L), eventStartCaptor.getValue().userIds());
    }

    private static @NotNull List<Event> getEventList() {
        return List.of(
                Event.builder().id(1L).endDate(LocalDateTime.now().minusDays(1)).build(),
                Event.builder().id(2L).endDate(LocalDateTime.now().minusDays(2)).build(),
                Event.builder().id(5L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(6L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(7L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(8L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(9L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(10L).endDate(LocalDateTime.now().minusDays(3)).build(),
                Event.builder().id(12L).endDate(LocalDateTime.now().minusDays(3)).build()
        );
    }

}
