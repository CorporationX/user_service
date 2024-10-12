package school.faang.user_service.config.redis.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPremiumBoughtEventPublisher implements PremiumBoughtEventPublisher{
    private final RedisTemplate<String, PremiumBoughtEventDto> premiumBoughtEventDtoRedisTemplate;
    private final ChannelTopic premiumBoughtEventTopic;

    @Override
    public void publish(List<PremiumBoughtEventDto> premiumBoughtEventDtos) {
        premiumBoughtEventDtoRedisTemplate.convertAndSend(premiumBoughtEventTopic.getTopic(), premiumBoughtEventDtos);
        log.info("Publish into topic: {}, message: {}", premiumBoughtEventTopic.getTopic(), premiumBoughtEventDtos);
    }
}
