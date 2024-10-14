package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.event.EventType;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.promotion.PromotionManagementService;
import school.faang.user_service.validator.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    private static final long EVENT_ID_ONE = 1L;
    private static final long EVENT_ID_TWO = 2L;
    private static final long USER_ID_IS_ONE = 1L;
    private EventDto eventDto;
    private Skill skill1;
    private Skill skill2;
    private List<Skill> skills;
    private Event event;

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EventValidator eventValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private PromotionManagementService promotionManagementService;

    @Mock
    private List<EventFilter> eventFilters;

    @Mock
    private EventFilter eventFilter;

    @Nested
    class PositiveTests {

        private User user;
        private Event plannedEvent;
        private Event canceledEvent;

        @BeforeEach
        void init() {
            plannedEvent = Event.builder()
                    .id(EVENT_ID_ONE)
                    .status(EventStatus.PLANNED)
                    .build();

            canceledEvent = Event.builder()
                    .id(EVENT_ID_TWO)
                    .status(EventStatus.CANCELED)
                    .build();

            List<Event> events = new ArrayList<>();
            events.add(plannedEvent);
            events.add(canceledEvent);

            Promotion promotion = mock(Promotion.class);

            user = User.builder()
                    .id(USER_ID_IS_ONE)
                    .ownedEvents(events)
                    .promotions(List.of(promotion))
                    .build();

            eventDto = EventDto.builder()
                    .id(1L)
                    .title("Новое событие")
                    .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                    .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                    .ownerId(1L)
                    .description("description")
                    .relatedSkills(List.of(1L, 2L))
                    .location("location")
                    .maxAttendees(5)
                    .build();

            skill1 = Skill.builder()
                    .id(1L)
                    .build();

            skill2 = Skill.builder()
                    .id(2L)
                    .build();

            skills = List.of(skill1, skill2);

            event = Event.builder()
                    .id(1L)
                    .title("Новое событие")
                    .description("какое-то описание")
                    .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                    .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                    .location("location")
                    .maxAttendees(5)
                    .owner(user)
                    .relatedSkills(skills)
                    .type(EventType.GIVEAWAY)
                    .status(EventStatus.IN_PROGRESS)
                    .build();
        }

        @Test
        @DisplayName("If user has planned event than delete this event")
        void whenUserHasPlannedEventThenCancelItAndDeleteFromListUserEvents() {
            eventService.deactivatePlanningUserEventsAndDeleteEvent(user);

            assertEquals(EventStatus.CANCELED, plannedEvent.getStatus());

            verify(eventParticipationService)
                    .deleteParticipantsFromEvent(plannedEvent);
            verify(eventRepository)
                    .deleteById(plannedEvent.getId());

            assertFalse(user.getOwnedEvents().contains(plannedEvent));
            assertTrue(user.getOwnedEvents().contains(canceledEvent));
        }

        @Test
        @DisplayName("successful event creation")
        void whenCreateThenSaveEvent() {
            {
                Event savedEvent = new Event();
                EventDto resultDto = new EventDto();

                doNothing().when(eventValidator).validateEventDto(eventDto);
                doNothing().when(eventValidator).validateOwnerSkills(eventDto);
                when(eventMapper.toEvent(eventDto)).thenReturn(event);
                when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skills);
                when(userRepository.getById(eventDto.getOwnerId())).thenReturn(user);
                when(eventRepository.save(event)).thenReturn(event);
                when(eventMapper.toDto(event)).thenReturn(eventDto);

                EventDto returnedDto = eventService.create(eventDto);

                assertNotNull(event.getRelatedSkills());
                assertNotNull(event.getOwner());

                verify(eventValidator).validateEventDto(eventDto);
                verify(eventValidator).validateOwnerSkills(eventDto);
                verify(eventMapper).toEvent(eventDto);
                verify(skillRepository).findAllByUserId(eventDto.getOwnerId());
                verify(userRepository).getById(eventDto.getOwnerId());
                verify(eventRepository).save(event);
                verify(eventMapper).toDto(event);

                assertEquals(eventDto, returnedDto);
            }
        }

        @Test
        void testGetEventWhenEventExists() {
            long eventId = 1L;
            Event event = mock(Event.class);
            EventDto expectedEventDto = mock(EventDto.class);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(eventMapper.toDto(event)).thenReturn(expectedEventDto);

            EventDto result = eventService.getEvent(eventId);

            verify(eventRepository).findById(eventId);
            verify(eventMapper).toDto(event);
            assertEquals(expectedEventDto, result);
        }

        @Test
        void testGetEventWhenEventDoesNotExist() {
            long eventId = 1L;
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> eventService.getEvent(eventId));
            assertEquals("Event not found with id: " + eventId, exception.getMessage());
            verify(eventRepository).findById(eventId);
        }

        @Test
        void testDeleteEvent() {
            doNothing().when(eventRepository).deleteById(event.getId());

            eventService.deleteEvent(event.getId());

            verify(eventRepository).deleteById(event.getId());
        }

        @Test
        void testUpdateEventSuccess() {
            when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));

            doNothing().when(eventValidator).validateEventDto(eventDto);
            doNothing().when(eventValidator).validateOwnerSkills(eventDto);

            when(eventMapper.toEvent(eventDto)).thenReturn(event);
            when(skillRepository.findAllByUserId(eventDto.getOwnerId())).thenReturn(skills);
            when(userRepository.getById(eventDto.getOwnerId())).thenReturn(user);
            when(eventRepository.save(event)).thenReturn(event);
            when(eventMapper.toDto(event)).thenReturn(eventDto);

            EventDto result = eventService.updateEvent(eventDto);

            assertNotNull(event.getRelatedSkills());
            assertNotNull(event.getOwner());

            verify(eventRepository).findById(eventDto.getId());
            verify(eventValidator).validateEventDto(eventDto);
            verify(eventValidator).validateOwnerSkills(eventDto);
            verify(eventMapper).toEvent(eventDto);
            verify(eventRepository).save(event);
            verify(eventMapper).toDto(event);
            assertEquals(eventDto, result);
        }

        @Test
        void testUpdateEventWhenUserIsNotOwner() {
            when(eventRepository.findById(eventDto.getId())).thenReturn(Optional.of(event));
            user.setId(2L);
            event.setOwner(user);
            assertThrows(DataValidationException.class,
                    () -> eventService.updateEvent(eventDto));
            verify(eventRepository).findById(eventDto.getId());
        }

        @Test
        void testGetOwnedEvents() {
            long userId = 100L;
            List<Event> events = Arrays.asList(mock(Event.class), mock(Event.class));
            EventDto eventDto1 = mock(EventDto.class);
            EventDto eventDto2 = mock(EventDto.class);

            when(eventRepository.findAllByUserId(userId)).thenReturn(events);
            when(eventMapper.toDto(events.get(0))).thenReturn(eventDto1);
            when(eventMapper.toDto(events.get(1))).thenReturn(eventDto2);

            List<EventDto> result = eventService.getOwnedEvents(userId);

            verify(eventRepository).findAllByUserId(userId);
            verify(eventMapper).toDto(events.get(0));
            verify(eventMapper).toDto(events.get(1));
            assertEquals(Arrays.asList(eventDto1, eventDto2), result);
        }

        @Test
        void testGetParticipatedEvents() {
            long userId = 100L;
            List<Event> events = Arrays.asList(mock(Event.class), mock(Event.class));
            EventDto eventDto1 = mock(EventDto.class);
            EventDto eventDto2 = mock(EventDto.class);

            when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);
            when(eventMapper.toDto(events.get(0))).thenReturn(eventDto1);
            when(eventMapper.toDto(events.get(1))).thenReturn(eventDto2);

            List<EventDto> result = eventService.getParticipatedEvents(userId);

            verify(eventRepository).findParticipatedEventsByUserId(userId);
            verify(eventMapper).toDto(events.get(0));
            verify(eventMapper).toDto(events.get(1));
            assertEquals(Arrays.asList(eventDto1, eventDto2), result);
        }

        @Test
        @DisplayName("Successfully get filtered and prioritized events")
        void whenGetEventsByFilterThenSuccess() {
            EventFilterDto filterDto = new EventFilterDto();
            List<Event> filteredEvents = List.of(event);

            doNothing().when(promotionManagementService).removeExpiredPromotions();
            when(eventFilters.stream()).thenReturn(Stream.of(eventFilter));
            when(eventFilter.isApplicable(filterDto)).thenReturn(true);
            when(eventFilter.toSpecification(filterDto)).thenReturn(mock(Specification.class));
            when(eventRepository.findAll(any(Specification.class))).thenReturn(filteredEvents);
            when(eventMapper.toDto(any(Event.class))).thenReturn(eventDto);

            List<EventDto> result = eventService.getEventsByFilter(filterDto);

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(eventMapper, times(1)).toDto(any(Event.class));
        }
    }
}