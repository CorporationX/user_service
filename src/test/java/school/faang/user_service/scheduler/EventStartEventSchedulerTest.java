package school.faang.user_service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.event.EventStartEvent;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.service.event.EventService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventStartEventSchedulerTest {

    @Mock
    private EventService eventService;

    @Mock
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Mock
    private EventStartEventPublisher eventStartEventPublisher;

    @InjectMocks
    private EventStartEventScheduler eventStartEventScheduler;

    @BeforeEach
    public void setUp() {
        eventStartEventScheduler.setDurationsToPublishEventStartEvent(List.of(60000L));
        eventStartEventScheduler.setUploadEventDaysBatch(2L);
    }

    @Test
    void testLoadUpcomingEvents() {
        List<Event> mockEvents = List.of(
                Event.builder()
                        .id(1L)
                        .startDate(LocalDateTime.now().plusDays(1))
                        .build(),
                Event.builder()
                        .id(2L)
                        .startDate(LocalDateTime.now().plusDays(2))
                        .build()
        );
        when(eventService.findEventsByStartDateBetween(any(), any())).thenReturn(mockEvents);

        eventStartEventScheduler.loadUpcomingEvents();

        verify(eventService).findEventsByStartDateBetween(any(), any());
        assertEquals(List.of(1L, 2L), eventStartEventScheduler.getEventsToPublish());
    }

    @Test
    void testClearPublishedEvents() {
        eventStartEventScheduler.setPublishedEvents(new ArrayList<>(List.of(1L, 2L)));
        when(eventService.findAllEventsByIds(anyList())).thenReturn(List.of(Event.builder()
                .id(1L)
                .startDate(LocalDateTime.now())
                .build()));

        eventStartEventScheduler.clearPublishedEvents();

        verify(eventService).findAllEventsByIds(anyList());
        assertEquals(List.of(1L), eventStartEventScheduler.getPublishedEvents());
    }

    @Test
    void testSchedulePreStartNotifications_NoEvents() {
        eventStartEventScheduler.setEventsToPublish(new ArrayList<>());

        eventStartEventScheduler.schedulePreStartNotifications();

        verifyNoInteractions(eventService, threadPoolTaskScheduler);
    }



    @Test
    void testSchedulePreStartNotifications_WithEvents() {
        eventStartEventScheduler.setEventsToPublish(List.of(1L));
        Event mockEvent = Event.builder()
                .id(1L)
                .startDate(LocalDateTime.now().plusMinutes(10))
                .build();

        when(eventService.findEventWithAttendeesById(1L)).thenReturn(mockEvent);

        eventStartEventScheduler.schedulePreStartNotifications();

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> instantCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(threadPoolTaskScheduler).schedule(runnableCaptor.capture(), instantCaptor.capture());
        Instant capturedInstant = instantCaptor.getValue();
        assertTrue(capturedInstant.isAfter(Instant.now()));
        assertNotNull(runnableCaptor.getValue());
        assertNotNull(instantCaptor.getValue());
    }


    @Test
    void testScheduleNotificationIfNeeded_ImmediateExecution() {
        Event mockEvent = Event.builder()
                .id(1L)
                .startDate(LocalDateTime.now().minusMinutes(5))
                .build();
        User mockUser = User.builder()
                .id(101L)
                .username("Test User")
                .build();
        mockEvent.setAttendees(List.of(mockUser));

        when(eventService.findEventWithAttendeesById(1L)).thenReturn(mockEvent);

        eventStartEventScheduler.scheduleNotificationIfNeeded(1L, 0);

        ArgumentCaptor<EventStartEvent> captor = ArgumentCaptor.forClass(EventStartEvent.class);
        verify(eventStartEventPublisher).publish(captor.capture());

        EventStartEvent publishedEvent = captor.getValue();
        assertNotNull(publishedEvent);
        assertEquals(1L, publishedEvent.eventId());
        assertEquals(List.of(101L), publishedEvent.attendeesIds());
        assertTrue(eventStartEventScheduler.getPublishedEvents().contains(1L));
    }

    @Test
    void testScheduleNotificationIfNeeded_ScheduledExecution() {
        Event mockEvent = Event.builder()
                .id(2L)
                .startDate(LocalDateTime.now().plusMinutes(10))
                .build();
        User mockUser = User.builder()
                .id(102L)
                .username("Test User")
                .build();
        mockEvent.setAttendees(List.of(mockUser));
        when(eventService.findEventWithAttendeesById(2L)).thenReturn(mockEvent);

        eventStartEventScheduler.scheduleNotificationIfNeeded(2L, 5 * 60 * 1000);

        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Instant> dateCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(threadPoolTaskScheduler).schedule(runnableCaptor.capture(), dateCaptor.capture());

        Runnable scheduledTask = runnableCaptor.getValue();
        assertNotNull(scheduledTask);

        Instant scheduledTime = dateCaptor.getValue();
        assertNotNull(scheduledTime);
        assertTrue(scheduledTime.isBefore(mockEvent.getStartDate().toInstant(ZoneOffset.UTC)));
    }
}
