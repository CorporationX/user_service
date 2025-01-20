package school.faang.user_service.service.event;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validation.event.EventValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private List<EventFilter> eventFilters;
    @Spy
    private EventValidator eventValidator;
    @InjectMocks
    private EventService eventService;

    private Long eventId;
    private Long userId;
    private Event event;
    private Event event2;
    private User owner;
    private Skill skill;
    private List<Event> events;
    private List<Skill> skills;
    private EventFilterDto eventFilterDto;
    private EventDto eventDto;

    @BeforeEach
    public void setUp() {
        eventId = 1L;
        String eventTitle = "Test Event";

        event = new Event();
        event.setId(eventId);
        event.setTitle(eventTitle);

        skill = new Skill();
        skill.setId(1L);
        skills = List.of(skill);
        event.setRelatedSkills(skills);

        userId = 1L;
        owner = new User();
        owner.setId(userId);
        owner.setSkills(skills);
        event.setOwner(owner);

        events = new ArrayList<>();
        events.add(event);

        event2 = new Event();
        event2.setId(eventId);
        event2.setTitle("Test Event2");
        event2.setOwner(owner);
        event2.setRelatedSkills(skills);

        eventFilterDto = new EventFilterDto();
        eventFilterDto.setTitle(eventTitle);

        eventDto = EventDto.builder()
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(event);
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(owner));

        Event createdEvent = eventService.create(event);

        assertNotNull(createdEvent);
        assertEquals("Test Event", createdEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testCreateEventWithoutSkills() {
        event.setRelatedSkills(new ArrayList<>());

        assertThrows(DataValidationException.class, () -> eventService.create(event));
    }

    @Test
    public void testGetEvent() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event foundEvent = eventService.getEvent(eventId);

        assertNotNull(foundEvent);
        assertEquals(eventId, foundEvent.getId());
        assertEquals("Test Event", foundEvent.getTitle());
        verify(eventRepository, times(1)).findById(eventId);
    }

    @Test
    public void testGetEventsByFilter() {
        when(eventRepository.findAll()).thenReturn(events);

        List<Event> foundEvents = eventService.getEventsByFilter(eventFilterDto);

        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testDeleteEvent() {
        doNothing().when(eventRepository).deleteById(eventId);

        eventService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testUpdateEvent() {
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event2));
        when(eventRepository.save(any(Event.class))).thenReturn(event2);
        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(skillRepository.findById(skill.getId())).thenReturn(Optional.of(skill));

        Event updatedEvent = eventService.updateEvent(event2);

        assertNotNull(updatedEvent);
        assertEquals("Test Event2", updatedEvent.getTitle());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    public void testGetOwnedEvents() {
        when(eventRepository.findAllByUserId(userId)).thenReturn(events);

        List<Event> foundEvents = eventService.getOwnedEvents(userId);

        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
        verify(eventRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetParticipatedEvents() {
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(events);

        List<Event> foundEvents = eventService.getParticipatedEvents(userId);

        assertNotNull(foundEvents);
        assertEquals(events.size(), foundEvents.size());
        assertEquals(events.get(0).getTitle(), foundEvents.get(0).getTitle());
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
    }

    @Test
    public void testIsValid_StartTimeAfterEndTime() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);

        assertFalse(eventValidator.isValid(eventDto, context));
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate("Start time must be before end");
        verify(builder).addPropertyNode("startTime");
        verify(nodeBuilder).addConstraintViolation();
    }
}
