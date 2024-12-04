package school.faang.user_service.service.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.dto.event.EventTimeToStart;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.redis.RedisMessagePublisher;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class EventStartServiceTest {

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Mock
    private EventParticipationService eventParticipationService;

    @Mock
    private EventRepository eventRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private EventStartService eventStartService;

    @Captor
    ArgumentCaptor<EventStartEventDto> captorEventStartEvent;

    @Test
    void scheduledAnnotationTest() throws NoSuchMethodException {
        String cron = eventStartService.getClass()
                .getMethod("checkEventsThatStarted")
                .getAnnotation(Scheduled.class)
                .cron();

        assertEquals(cron, "${check-start-event.scheduler.cron}");
    }

    @Test
    void eventStartNowTest() {
        Event firstEvent = new Event();
        firstEvent.setStartDate(LocalDateTime.now());
        List<Event> events = List.of(firstEvent);
        when(eventRepository.findNearbyUnstartedEvents()).thenReturn(events);

        eventStartService.checkEventsThatStarted();

        verify(redisMessagePublisher, times(1)).publish(captorEventStartEvent.capture());

        assertEquals(EventTimeToStart.START_NOW, captorEventStartEvent.getValue().getTimeBeforeStart());
    }

    @Test
    void eventStartIn10MinTest() {
        Event firstEvent = new Event();
        firstEvent.setStartDate(LocalDateTime.now().plusMinutes(10).plusSeconds(30));
        List<Event> events = List.of(firstEvent);
        when(eventRepository.findNearbyUnstartedEvents()).thenReturn(events);

        eventStartService.checkEventsThatStarted();

        verify(redisMessagePublisher, times(1)).publish(captorEventStartEvent.capture());

        assertEquals(EventTimeToStart.START_THROUGH_10_MIN, captorEventStartEvent.getValue().getTimeBeforeStart());
    }

    @Test
    void eventStartIn1HourTest() {
        Event firstEvent = new Event();
        firstEvent.setStartDate(LocalDateTime.now().plusHours(1).plusSeconds(30));
        List<Event> events = List.of(firstEvent);
        when(eventRepository.findNearbyUnstartedEvents()).thenReturn(events);

        eventStartService.checkEventsThatStarted();

        verify(redisMessagePublisher, times(1)).publish(captorEventStartEvent.capture());

        assertEquals(EventTimeToStart.START_THROUGH_1_HOUR, captorEventStartEvent.getValue().getTimeBeforeStart());
    }

    @Test
    void eventStartIn5HourTest() {
        Event firstEvent = new Event();
        firstEvent.setStartDate(LocalDateTime.now().plusHours(5).plusSeconds(30));
        List<Event> events = List.of(firstEvent);
        when(eventRepository.findNearbyUnstartedEvents()).thenReturn(events);

        eventStartService.checkEventsThatStarted();

        verify(redisMessagePublisher, times(1)).publish(captorEventStartEvent.capture());

        assertEquals(EventTimeToStart.START_THROUGH_5_HOURS, captorEventStartEvent.getValue().getTimeBeforeStart());
    }

    @Test
    void eventStartIn1DayTest() {
        Event firstEvent = new Event();
        firstEvent.setStartDate(LocalDateTime.now().plusDays(1));
        List<Event> events = List.of(firstEvent);
        when(eventRepository.findNearbyUnstartedEvents()).thenReturn(events);

        eventStartService.checkEventsThatStarted();

        verify(redisMessagePublisher, times(1)).publish(captorEventStartEvent.capture());

        assertEquals(EventTimeToStart.START_THROUGH_1_DAY, captorEventStartEvent.getValue().getTimeBeforeStart());
    }
}
