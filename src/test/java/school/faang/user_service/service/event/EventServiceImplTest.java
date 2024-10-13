package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.redis.event.EventStartEvent;
import school.faang.user_service.redis.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;
import school.faang.user_service.service.event.filters.EventLocationFilter;
import school.faang.user_service.service.event.filters.EventTitleFilter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EventStartEventPublisher eventStartEventPublisher;
    @InjectMocks
    private EventServiceImpl eventServiceImpl;

    private Event event;
    private Event otherEvent;
    private User user;
    private Event existingEvent;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        skill1.setTitle("Spring");

        Skill skill2 = new Skill();
        skill2.setId(2L);
        skill2.setTitle("Java");

        List<Skill> relatedSkills = Arrays.asList(skill1, skill2);
        user.setSkills(relatedSkills);

        event = new Event();
        event.setId(100L);
        event.setTitle("Webinar Java");
        event.setOwner(user);
        event.setRelatedSkills(relatedSkills);

        otherEvent = new Event();
        otherEvent.setId(101L);
        otherEvent.setTitle("Webinar Spring");
        otherEvent.setOwner(user);

        existingEvent = new Event();
        existingEvent.setId(100L);
        existingEvent.setTitle("Existing Event");
        existingEvent.setOwner(user);
        existingEvent.setRelatedSkills(relatedSkills);
    }

    @Test
    public void create_ShouldCreateEvent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventServiceImpl.create(event);

        verify(userRepository).findById(1L);
        verify(eventRepository).save(event);

        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getOwner(), result.getOwner());
        assertEquals(event.getRelatedSkills(), result.getRelatedSkills());
    }

    @Test
    public void getEvent_ShouldReturnEvent_WhenEventExists() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Event result = eventServiceImpl.getEvent(100L);

        verify(eventRepository).findById(100L);

        assertEquals(event.getId(), result.getId());
        assertEquals(event.getTitle(), result.getTitle());
        assertEquals(event.getOwner(), result.getOwner());
        assertEquals(event.getRelatedSkills(), result.getRelatedSkills());
    }

    @Test
    public void updateEvent_ShouldUpdateEvent() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(existingEvent));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(existingEvent);

        Event result = eventServiceImpl.updateEvent(event);

        verify(eventRepository).findById(100L);
        verify(userRepository).findById(1L);
        verify(eventRepository).save(existingEvent);

        assertEquals(existingEvent.getTitle(), result.getTitle());
        assertEquals(existingEvent.getOwner(), result.getOwner());
        assertEquals(existingEvent.getRelatedSkills(), result.getRelatedSkills());
    }

    @Test
    public void getEventsByFilter_WithRealFilters_ShouldReturnFilteredEvents() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(event, existingEvent));

        EventLocationFilter locationFilter = new EventLocationFilter();
        EventTitleFilter titleFilter = new EventTitleFilter();

        List<EventFilter> realFilters = Arrays.asList(locationFilter, titleFilter);

        eventServiceImpl = new EventServiceImpl(
                eventRepository, userRepository, realFilters, eventStartEventPublisher);

        EventFilters filters = new EventFilters();
        filters.setTitle("Java");

        List<Event> result = eventServiceImpl.getEventsByFilter(filters);

        assertEquals(1, result.size());
        assertEquals("Webinar Java", result.get(0).getTitle());
    }

    @Test
    public void getOwnedEvents_ShouldReturnEventsCreatedByUser() {
        when(eventRepository.findAllByUserId(anyLong())).thenReturn(Arrays.asList(event, otherEvent));

        List<Event> result = eventServiceImpl.getOwnedEvents(user.getId());

        assertEquals(2, result.size());
        assertEquals("Webinar Java", result.get(0).getTitle());
        assertEquals("Webinar Spring", result.get(1).getTitle());

        verify(eventRepository).findAllByUserId(user.getId());
    }

    @Test
    public void getParticipatedEvents_ShouldReturnEventsUserParticipatedIn() {
        when(eventRepository.findParticipatedEventsByUserId(anyLong())).thenReturn(Arrays.asList(event, otherEvent));

        List<Event> result = eventServiceImpl.getParticipatedEvents(user.getId());

        assertEquals(2, result.size());
        assertEquals("Webinar Java", result.get(0).getTitle());
        assertEquals("Webinar Spring", result.get(1).getTitle());

        verify(eventRepository).findParticipatedEventsByUserId(user.getId());
    }

    @Test
    public void deleteEvent_ShouldDeleteEvent() {
        doNothing().when(eventRepository).deleteById(anyLong());

        eventServiceImpl.deleteEvent(1L);

        verify(eventRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testStartEventsFromPeriod_Success() {
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = from.plusMinutes(1);
        event.setAttendees(List.of(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()));
        otherEvent.setAttendees(List.of(
                User.builder().id(1L).build()));

        when(eventRepository.findAllByStatusAndStartDateBetween(any(EventStatus.class), eq(from), eq(to)))
                .thenReturn(Arrays.asList(event, otherEvent));
        doNothing().when(eventStartEventPublisher).publish(any(EventStartEvent.class));
        when(eventRepository.saveAll(anyList())).thenReturn(Arrays.asList(event, otherEvent));

        eventServiceImpl.startEventsFromPeriod(from, to);

        verify(eventStartEventPublisher, times(2)).publish(any(EventStartEvent.class));
    }
}