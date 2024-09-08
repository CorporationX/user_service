package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.UserService;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.test_data.event.TestDataEvent;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @InjectMocks
    private EventService eventService;
    private EventRepository eventRepository;
    private EventMapper eventMapper;
    private UserService userService;
    private TestDataEvent testDataEvent;
    private EventDto eventDto;
    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        eventRepository = Mockito.mock(EventRepository.class);
        eventMapper = Mockito.mock(EventMapper.class);
        userService = Mockito.mock(UserService.class);
        List<EventFilter> filters = List.of(mock(EventFilter.class));
        eventService = new EventService(eventRepository, eventMapper, userService, filters);

        testDataEvent = new TestDataEvent();
        user = testDataEvent.getUser();
        event = testDataEvent.getEvent();
        eventDto = testDataEvent.getEventDto();
    }

    @Nested
    class PositiveTests {
        @Test
        void testCreateEvent_Success() {
            Event event = new Event();
            eventDto.setOwnerId(user.getId());

            when(eventMapper.toEntity(eventDto)).thenReturn(event);
            when(userService.getUser(user.getId())).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            var result = eventService.createEvent(eventDto);
            assertNotNull(result);
            assertEquals(eventDto, result);

            verify(userService, atLeastOnce()).getUser(user.getId());
            verify(eventRepository, atLeastOnce()).save(event);
        }

        @Test
        void testGetEvent_Success() {
            when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            EventDto result = eventService.getEvent(1L);
            assertNotNull(result);
            assertEquals(eventDto, result);

            verify(eventRepository, atLeastOnce()).findById(1L);
        }

        @Test
        void testGetEventsByFilters_Success() {
            Event event1 = new Event();
            Event event2 = new Event();
            Event event3 = new Event();
            List<Event> events = List.of(event1, event2,event3);
            EventFilterDto filter = testDataEvent.getEventFilterDto();

            when(eventRepository.findAll()).thenReturn(events);
            when(eventMapper.toDto(event1)).thenReturn(new EventDto());
            when(eventMapper.toDto(event2)).thenReturn(new EventDto());
            when(eventMapper.toDto(event3)).thenReturn(new EventDto());

            List<EventDto> result = eventService.getEventsByFilters(filter);
            assertEquals(events.size(), result.size());

            verify(eventRepository, atLeastOnce()).findAll();
            verify(eventMapper, times(events.size())).toDto(any(Event.class));
        }

        @Test
        public void testDeleteEvent_Success() {
            eventService.deleteEvent(eventDto.getId());

            verify(eventRepository, atLeastOnce()).deleteById(eventDto.getId());
        }

        @Test
        void testUpdateEvent_Success() {
            EventDto eventDto2 = testDataEvent.getEventDto2();

            when(eventMapper.toEntity(eventDto2)).thenReturn(event);
            when(userService.getUser(user.getId())).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(eventDto2);

            EventDto result = eventService.updateEvent(eventDto2);
            assertNotNull(result);
            assertEquals(eventDto2, result);
        }

        @Test
        void testGetEventsOwner_Success() {
            List<Event> eventList = List.of(event);
            List<EventDto> eventDtoList = List.of(eventDto);

            when(eventRepository.findAllByUserId(user.getId())).thenReturn(eventList);
            when(eventMapper.toDtoList(eventList)).thenReturn(eventDtoList);

            List<EventDto> result = eventService.getEventsOwner(user.getId());
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(eventDto, result.get(0));

            verify(eventRepository, atLeastOnce()).findAllByUserId(user.getId());
        }

        @Test
        void getEventParticipants_Success() {
            List<Event> eventList = List.of(event);
            List<EventDto> eventDtoList = List.of(eventDto);

            when(eventRepository.findParticipatedEventsByUserId(user.getId())).thenReturn(eventList);
            when(eventMapper.toDtoList(eventList)).thenReturn(eventDtoList);

            List<EventDto> result = eventService.getEventParticipants(user.getId());
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(eventDto, result.get(0));

            verify(eventRepository, atLeastOnce()).findParticipatedEventsByUserId(user.getId());
        }
    }

    @Nested
    class NegativeTest {
        @Test
        public void testGetEvent_NotFound_throwDataValidationException() {
            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            var exception = assertThrows(DataValidationException.class,
                    () -> eventService.getEvent(1L)
            );

            assertEquals("Event with ID: 1 not found.", exception.getMessage());

            verify(eventRepository, atLeastOnce()).findById(1L);
        }
    }
}
