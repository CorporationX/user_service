package school.faang.user_service.service.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.filter.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.common.RecordNotFoundException;
import school.faang.user_service.exception.event.EventCreationNotAllowedException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.entity.event.EventStatus.IN_PROGRESS;
import static school.faang.user_service.entity.event.EventStatus.PLANNED;
import static school.faang.user_service.entity.event.EventType.MEETING;
import static school.faang.user_service.entity.event.EventType.WEBINAR;
import static school.faang.user_service.util.LogsConstants.DELETED_EVENT_MESSAGE;
import static school.faang.user_service.util.LogsConstants.EMPTY_FILTER;
import static school.faang.user_service.util.LogsConstants.EVENT_NOT_FOUND;
import static school.faang.user_service.util.LogsConstants.NOT_ENOUGH_SKILLS;
import static school.faang.user_service.util.LogsConstants.USER_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private EventValidation eventValidation;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private EventServiceImpl eventService;
    User user;
    User userWithoutSkills;
    Event event;
    Event eventUpdates;
    Event eventWithoutSkills;
    List<Skill> skills;
    Skill skill1;
    Skill skill2;
    List<Long> skillIds;
    Long ownerId = 1L;
    Long userWithoutSkillsId = 2L;
    Long eventId = 1L;
    Long eventWithoutSkillsId = 2L;
    String eventTitle = "Test Event";
    String eventWithoutSkillsTitle = "Event without Skills";
    LocalDateTime baseTime = LocalDateTime.of(2025, 5, 1, 12, 0);
    String titleFilter = "Java";
    List<Event> expectedEvents;
    Long nonExistentId = 999L;
    List<Event> events;

    @BeforeEach
    void setUp() {
        skills = new ArrayList<>();
        skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Java");
        skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Spring");
        skills.add(skill1);
        skills.add(skill2);

        user = new User();
        user.setId(1L);
        user.setUsername("JohnDoe");
        user.setSkills(skills);

        userWithoutSkills = new User();
        userWithoutSkills.setId(userWithoutSkillsId);
        userWithoutSkills.setUsername("Michael Jackson");

        skillIds = Arrays.asList(1L, 2L);
        event = Event.builder()
                .id(eventId)
                .title(eventTitle)
                .description("Test Description")
                .startDate(baseTime.plusDays(1))
                .endDate(baseTime.plusDays(2))
                .location("Test Location")
                .maxAttendees(10)
                .owner(user)
                .relatedSkills(skills)
                .type(WEBINAR)
                .status(PLANNED)
                .build();
        eventUpdates = Event.builder()
                .title("Updated Test Event")
                .description("Updated Test Description")
                .startDate(baseTime.plusDays(3))
                .endDate(baseTime.plusDays(4))
                .location("Updated Test Location")
                .maxAttendees(15)
                .type(MEETING)
                .status(IN_PROGRESS)
                .build();

        eventWithoutSkills = Event.builder()
                .id(eventWithoutSkillsId)
                .title(eventWithoutSkillsTitle)
                .description("Test Description")
                .startDate(baseTime.plusDays(1))
                .endDate(baseTime.plusDays(2))
                .location("Test Location")
                .maxAttendees(10)
                .owner(user)
                .type(WEBINAR)
                .status(PLANNED)
                .build();

        expectedEvents = List.of(event);

        events = List.of(event, eventWithoutSkills);
    }

    // тесты на метод create()
    @Test
    void testCreateEventShouldSuccessfullyCreateEventWithValidData() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        when(eventRepository.save(event)).thenReturn(event);

        Event createdEvent = eventService.create(event, skillIds);

        assertEquals(eventId, createdEvent.getId());
        assertEquals(eventTitle, createdEvent.getTitle());
    }

    @Test
    void testCreateEventShouldThrowExceptionWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(USER_NOT_FOUND, ownerId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.create(event, skillIds));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreateEventShouldThrowExceptionWhenUserDoesNotHaveAllRequiredSkills() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        String exceptionMessage = String.format(NOT_ENOUGH_SKILLS, skill2);

        doThrow(new EventCreationNotAllowedException(exceptionMessage))
                .when(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);

        EventCreationNotAllowedException exception = assertThrows(EventCreationNotAllowedException.class,
                () -> eventService.create(event, skillIds));

        assertTrue(exception.getMessage().contains(exceptionMessage));
        verify(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);
    }

    @Test
    void testCreateEventShouldCreateEventWhenEventWithEmptySkillsList() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        when(skillRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(eventRepository.save(eventWithoutSkills)).thenReturn(eventWithoutSkills);

        Event createdEvent = eventService.create(eventWithoutSkills, Collections.emptyList());

        assertEquals(eventWithoutSkillsId, createdEvent.getId());
        assertEquals(eventWithoutSkillsTitle, createdEvent.getTitle());
    }

    // тесты на метод updateEvent()
    @Test
    void testUpdateEventShouldSuccessfullyUpdateEventWithValidData() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(eventUpdates, Collections.emptyList(), eventId);

        assertEquals(eventId, updatedEvent.getId());
        assertEquals("Updated Test Event", updatedEvent.getTitle());
        assertEquals("Updated Test Description", updatedEvent.getDescription());
        assertEquals(baseTime.plusDays(3), updatedEvent.getStartDate());
        assertEquals(baseTime.plusDays(4), updatedEvent.getEndDate());
        assertEquals("Updated Test Location", updatedEvent.getLocation());
        assertEquals(15, updatedEvent.getMaxAttendees());
        assertEquals(MEETING, updatedEvent.getType());
        assertEquals(IN_PROGRESS, updatedEvent.getStatus());
    }

    @Test
    void testUpdateEventShouldThrowExceptionWhenUserNotFoundDuringUpdate() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(USER_NOT_FOUND, ownerId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.updateEvent(eventUpdates, Collections.emptyList(), eventId));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testUpdateEventShouldThrowExceptionWhenEventNotFound() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(EVENT_NOT_FOUND, eventId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.updateEvent(eventUpdates, Collections.emptyList(), eventId));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testUpdateEventShouldThrowExceptionWhenUserIsNotEventOwner() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        String exceptionMessage = "У вас нет прав на редактирование этого ивента";

        doThrow(new IllegalArgumentException(exceptionMessage))
                .when(eventValidation).isUserEventOwner(userWithoutSkillsId, event.getOwner().getId());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.updateEvent(eventUpdates, Collections.emptyList(), eventId));

        assertTrue(exception.getMessage().contains(exceptionMessage));
        verify(eventValidation).isUserEventOwner(userWithoutSkillsId, event.getOwner().getId());
    }

    @Test
    void testUpdateEventShouldThrowExceptionWhenUserDoesNotHaveRequiredSkillsForUpdate() {
        when(userContext.getUserId()).thenReturn(userWithoutSkillsId);
        when(userRepository.findById(userWithoutSkillsId)).thenReturn(Optional.of(userWithoutSkills));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        String exceptionMessage = String.format(NOT_ENOUGH_SKILLS, skillIds);

        doThrow(new EventCreationNotAllowedException(exceptionMessage))
                .when(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);
        EventCreationNotAllowedException exception = assertThrows(EventCreationNotAllowedException.class,
                () -> eventService.updateEvent(eventUpdates, skillIds, eventId));

        assertTrue(exception.getMessage().contains(exceptionMessage));
        verify(eventValidation).validateUserHasAllEventSkills(skillIds, userWithoutSkills);
    }

    @Test
    void testUpdateEventShouldUpdateOnlyProvidedFieldsAndLeaveOthersUnchanged() {
        Event partialUpdates = Event.builder()
                .title("Partial Update Title")
                .description("Partial Update Description")
                .build();
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);

        Event updatedEvent = eventService.updateEvent(partialUpdates, Collections.emptyList(), eventId);

        assertEquals("Partial Update Title", updatedEvent.getTitle());
        assertEquals("Partial Update Description", updatedEvent.getDescription());
        assertEquals(event.getStartDate(), updatedEvent.getStartDate());
        assertEquals(event.getEndDate(), updatedEvent.getEndDate());
        assertEquals(event.getLocation(), updatedEvent.getLocation());
        assertEquals(event.getMaxAttendees(), updatedEvent.getMaxAttendees());
        assertEquals(event.getType(), updatedEvent.getType());
        assertEquals(event.getStatus(), updatedEvent.getStatus());
    }

    @Test
    void testUpdateEventShouldUpdateRelatedSkillsWhenProvided() {
        Event updatesWithSkills = Event.builder()
                .relatedSkills(new ArrayList<>())
                .build();
        when(userContext.getUserId()).thenReturn(ownerId);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(eventWithoutSkillsId)).thenReturn(Optional.of(eventWithoutSkills));
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);
        when(eventRepository.save(eventWithoutSkills)).thenReturn(eventWithoutSkills);

        Event updatedEvent = eventService.updateEvent(updatesWithSkills, skillIds, eventWithoutSkillsId);

        assertEquals(skills, updatedEvent.getRelatedSkills());
        verify(skillRepository).findAllById(skillIds);
    }

    // тесты на метод getEventsByFilter()
    @Test
    void testGetEventsByFilterShouldFilterByTitle() {
        EventFilterDto filterByTitle = EventFilterDto.builder()
                .title(titleFilter)
                .build();
        when(eventRepository.findAll(Mockito.<Specification<Event>>any())).thenReturn(expectedEvents);

        List<Event> result = eventService.getEventsByFilter(filterByTitle);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findAll(Mockito.<Specification<Event>>any());
    }

    @Test
    void testGetEventsByFilterShouldFilterByStartDate() {
        EventFilterDto filterByStartDate = EventFilterDto.builder()
                .startDate(baseTime)
                .build();
        when(eventRepository.findAll(Mockito.<Specification<Event>>any())).thenReturn(expectedEvents);

        List<Event> result = eventService.getEventsByFilter(filterByStartDate);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findAll(Mockito.<Specification<Event>>any());
    }

    @Test
    void testGetEventsByFilterShouldFilterByOwnerId() {
        EventFilterDto filterByOwnerId = EventFilterDto.builder()
                .ownerId(ownerId)
                .build();
        when(eventRepository.findAll(Mockito.<Specification<Event>>any())).thenReturn(expectedEvents);

        List<Event> result = eventService.getEventsByFilter(filterByOwnerId);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findAll(Mockito.<Specification<Event>>any());
    }

    @Test
    void testGetEventsByFilterShouldFilterByCombinedCriteria() {
        EventFilterDto filter = EventFilterDto.builder()
                .title(titleFilter)
                .startDate(baseTime)
                .ownerId(ownerId)
                .build();

        when(eventRepository.findAll(Mockito.<Specification<Event>>any())).thenReturn(expectedEvents);

        List<Event> result = eventService.getEventsByFilter(filter);

        assertEquals(expectedEvents, result);
        verify(eventRepository).findAll(Mockito.<Specification<Event>>any());
    }

    @Test
    void testGetEventsByFilterShouldReturnAllEventsWithEmptyFilter() {
        EventFilterDto emptyFilter = new EventFilterDto();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventsByFilter(emptyFilter));

        assertTrue(exception.getMessage().contains(EMPTY_FILTER));
    }

    @Test
    void testGetEventsByFilterShouldReturnEmptyListWhenNoMatches() {
        EventFilterDto filter = EventFilterDto.builder()
                .title("NonExistentTitle")
                .build();

        when(eventRepository.findAll(Mockito.<Specification<Event>>any())).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getEventsByFilter(filter);

        assertTrue(result.isEmpty());
        verify(eventRepository).findAll(Mockito.<Specification<Event>>any());
    }

    // тесты на метод getEvent()
    @Test
    void testGetEventShouldReturnEventWhenExists() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event eventFromDb = eventService.getEvent(eventId);

        assertEquals(event, eventFromDb);
        verify(eventRepository).findById(eventId);
    }

    @Test
    void testGetEventShouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(EVENT_NOT_FOUND, nonExistentId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.getEvent(nonExistentId));

        assertEquals(expectedMessage, exception.getMessage());
        verify(eventRepository).findById(nonExistentId);
    }

    // тесты на метод getOwnedEvents()
    @Test
    void testGetOwnedEventsShouldReturnOwnedEvents() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(eventRepository.findAllByUserId(ownerId)).thenReturn(events);

        List<Event> ownedEvents = eventService.getOwnedEvents();

        assertTrue(ownedEvents.containsAll(events));
        verify(eventRepository).findAllByUserId(ownerId);
    }

    @Test
    void testGetOwnedEventsShouldReturnEmptyListIfNoOwnedEventsExist() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(eventRepository.findAllByUserId(ownerId)).thenReturn(Collections.emptyList());

        List<Event> ownedEvents = eventService.getOwnedEvents();
        assertTrue(ownedEvents.isEmpty());
        verify(eventRepository).findAllByUserId(ownerId);
    }

    // тесты на метод getParticipatedEvents()
    @Test
    void testGetParticipatedEventsShouldReturnEventsUserParticipatesIn() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(eventRepository.findParticipatedEventsByUserId(ownerId)).thenReturn(events);

        List<Event> result = eventService.getParticipatedEvents();

        assertEquals(events, result);
        verify(eventRepository).findParticipatedEventsByUserId(ownerId);
    }

    @Test
    void testGetParticipatedEventsShouldReturnEmptyListWhenUserDoesNotParticipateInAnyEvents() {
        when(userContext.getUserId()).thenReturn(ownerId);
        when(eventRepository.findParticipatedEventsByUserId(ownerId)).thenReturn(Collections.emptyList());

        List<Event> result = eventService.getParticipatedEvents();

        assertTrue(result.isEmpty());
        verify(eventRepository).findParticipatedEventsByUserId(ownerId);
    }

    // тесты на метод deleteEvent()
    @Test
    void testDeleteEventShouldRemoveEventWhenItExists() {
        when(eventRepository.findOwnerIdByEventId(eventId)).thenReturn(Optional.of(ownerId));
        when(userContext.getUserId()).thenReturn(ownerId);
        String expectedResult = String.format(DELETED_EVENT_MESSAGE, eventId);

        String result = eventService.deleteEvent(eventId);

        assertEquals(expectedResult, result);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void testDeleteEventShouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findOwnerIdByEventId(nonExistentId)).thenReturn(Optional.empty());
        String expectedMessage = String.format(EVENT_NOT_FOUND, nonExistentId);

        RecordNotFoundException exception = assertThrows(RecordNotFoundException.class,
                () -> eventService.deleteEvent(nonExistentId));

        assertEquals(expectedMessage, exception.getMessage());
        verify(eventRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteEventShouldThrowExceptionWhenUserIsNotEventOwner() {
        when(eventRepository.findOwnerIdByEventId(eventId)).thenReturn(Optional.of(userWithoutSkillsId));
        when(userContext.getUserId()).thenReturn(ownerId);
        String expectedMessage = "У вас нет прав на удаление этого ивента";

        doThrow(new IllegalArgumentException(expectedMessage))
                .when(eventValidation).isUserEventOwner(ownerId, userWithoutSkillsId);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> eventService.deleteEvent(eventId));

        assertEquals(expectedMessage, exception.getMessage());
        verify(eventValidation).isUserEventOwner(ownerId, userWithoutSkillsId);
    }
}
