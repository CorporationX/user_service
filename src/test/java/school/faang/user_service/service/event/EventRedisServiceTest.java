package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventRedisMapperImpl;
import school.faang.user_service.model.event.EventRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventRedisServiceTest {
    @Mock
    private EventRedisRepository eventRedisRepository;
    @Spy
    private EventRedisMapperImpl eventRedisMapper;
    @Mock
    private RedisTtlProperties redisTtlProperties;
    @Captor
    private ArgumentCaptor<EventRedisModel> eventRedisModelCaptor;
    @InjectMocks
    private EventRedisService eventRedisService;
    private Event event;
    private static final long TTL = 30L;

    @BeforeEach
    public void setUp() {
        event = new Event();
        event.setId(13L);
    }

    @Test
    void testSaveEvent_successfully() {
        assertDoesNotThrow(() -> eventRedisService.saveEvent(event, TTL));

        verify(eventRedisRepository).save(eventRedisModelCaptor.capture());

        EventRedisModel capturedModel = eventRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(RedisKeyUtil.getSmallKeyById(event.getId()), capturedModel.getKey());
        assertEquals(event.getId(), capturedModel.getId());
    }

    @Test
    void testGetEventById_presentInRedis() {
        String expectedKey = RedisKeyUtil.getSmallKeyById(event.getId());
        EventRedisModel redisModel = new EventRedisModel();
        redisModel.setKey(expectedKey);

        when(eventRedisRepository.findById(expectedKey)).thenReturn(Optional.of(redisModel));
        when(eventRedisMapper.toEventEntity(redisModel)).thenReturn(event);

        Optional<Event> result = eventRedisService.getEventFromRedisById(event.getId());

        assertTrue(result.isPresent());
        assertEquals(event, result.get());
        verify(eventRedisRepository).findById(expectedKey);
        verify(eventRedisMapper).toEventEntity(redisModel);
    }

    @Test
    void testGetEventById_notPresentInRedis() {
        String expectedKey = RedisKeyUtil.getSmallKeyById(event.getId());

        when(eventRedisRepository.findById(expectedKey)).thenReturn(Optional.empty());

        Optional<Event> result = eventRedisService.getEventFromRedisById(event.getId());

        assertTrue(result.isEmpty());
        verify(eventRedisRepository).findById(expectedKey);
    }

    @Test
    void testUpdatePromotedEvent_saveUpdatedEvent() {
        assertDoesNotThrow(() -> eventRedisService.updatePromotedEvent(event));

        verify(eventRedisRepository).save(eventRedisModelCaptor.capture());

        EventRedisModel capturedModel = eventRedisModelCaptor.getValue();
        assertNotNull(capturedModel);
        assertEquals(event.getId(), capturedModel.getId());
    }
}
