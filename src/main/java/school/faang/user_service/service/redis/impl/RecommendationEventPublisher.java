package school.faang.user_service.service.redis.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.model.recommendation.RecommendationEvent;
import school.faang.user_service.service.redis.RedisService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationEventPublisher implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Override
    public void publish(RecommendationEvent message) {
        String topic = channelTopic.getTopic();
        redisTemplate.convertAndSend(topic, message);
        log.info("Сообщение {} отправлено в topic {}", message, topic);
    }
}
