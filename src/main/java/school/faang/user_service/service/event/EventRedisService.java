package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.redis.RedisTtlProperties;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventRedisMapper;
import school.faang.user_service.model.event.EventRedisModel;
import school.faang.user_service.repository.event.EventRedisRepository;
import school.faang.user_service.utils.redis.RedisKeyUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventRedisService {
    private final EventRedisRepository eventRedisRepository;
    private final EventRedisMapper eventRedisMapper;
    private final RedisTtlProperties redisTtlProperties;

    public void saveEvent(Event event, long ttl) {
        EventRedisModel eventRedisModel = eventRedisMapper.toEventRedisModel(event);
        log.debug("Mapping Event entity to EventRedisModel. Entity content: {}. RedisModel content: {}.",
                event, eventRedisModel);

        eventRedisModel.setTtl(ttl);

        String eventKey = RedisKeyUtil.getSmallKeyById(event.getId());
        eventRedisModel.setKey(eventKey);

        EventRedisModel savedEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been saved in redis", savedEvent);
    }

    public Optional<Event> getEventFromRedisById(long eventId) {
        String eventKey = RedisKeyUtil.getSmallKeyById(eventId);

        return eventRedisRepository.findById(eventKey)
                .map(eventRedisMapper::toEventEntity)
                .or(Optional::empty);
    }

    public void updatePromotedEvent(Event event) {
        EventRedisModel eventRedisModel = eventRedisMapper.toEventRedisModel(event);
        EventRedisModel savedEvent = eventRedisRepository.save(eventRedisModel);
        log.info("Event {} has been updated in redis", savedEvent);
    }

    @Async("addEventInRedisExecutor")
    public void addEventInRedis(Event event) {
        long ttl = redisTtlProperties.getEvent();
        saveEvent(event, ttl);
    }
}
