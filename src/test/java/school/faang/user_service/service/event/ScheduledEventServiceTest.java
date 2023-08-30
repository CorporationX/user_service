package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.CountdownPair;
import school.faang.user_service.dto.event.EventCountdown;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventStartEventMapper;
import school.faang.user_service.mapper.event.EventStartEventMapperImpl;
import school.faang.user_service.publisher.JsonObjectMapper;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledEventServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private JsonObjectMapper jsonObjectMapper;
    @Spy
    private EventStartEventMapper eventStartEventMapper = new EventStartEventMapperImpl();
    @Mock
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    @Mock
    private EventRepository eventRepository;

    private ScheduledEventService scheduledEventService;

    private Set<Integer> days;
    private Set<Integer> hours;
    private Set<Integer> minutes;

    @BeforeEach
    void setUp() {
        scheduledEventService = new ScheduledEventService(redisTemplate, jsonObjectMapper
                , eventStartEventMapper, scheduledThreadPoolExecutor, eventRepository);
        scheduledEventService.setEventStartEventTopicName("event_start_channel");
        scheduledEventService.setPublishEventStartNotification(true);
        days = Set.of(1);
        hours = Set.of(5, 1);
        minutes = Set.of(10);
    }

    @Test
    void mapToEventStartEventDtoTestThrowEntityNotFoundException() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> scheduledEventService.mapToEventStartEventDto(1,2,3));

        assertEquals("Event with id 1 has already been canceled", exception.getMessage());
    }

    @Test
    void sendScheduledEventStartEventTest() {
        LocalDateTime publishDate = LocalDateTime.now().plusMonths(1);

        scheduledEventService.sendScheduledEventStartEvent(days, hours, minutes, 1, publishDate);

        verify(scheduledThreadPoolExecutor, times(5))
                .schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
    }

    @Test
    void mapToEventStartEventDtoTest() {
        User first = User.builder()
                .id(1)
                .build();
        User second = User
                .builder()
                .id(2)
                .build();
        Event event = Event.builder()
                .id(1)
                .title("Birthday")
                .attendees(List.of(first, second))
                .build();

        CountdownPair countdownPair = new CountdownPair(EventCountdown.HOURS, 2);

        EventStartEventDto expected = EventStartEventDto.builder().
                id(1)
                .title("Birthday")
                .userIds(List.of(1L, 2L))
                .eventCountdown(countdownPair)
                .build();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        EventStartEventDto result = scheduledEventService.mapToEventStartEventDto(1, 1, 2);

        assertEquals(expected, result);

        verify(eventRepository).findById(1L);
    }
}