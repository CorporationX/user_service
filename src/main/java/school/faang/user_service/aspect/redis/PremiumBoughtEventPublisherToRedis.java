package school.faang.user_service.aspect.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.aspect.EventPublisher;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.redis.publisher.AbstractEventAggregator;

import java.time.temporal.ChronoUnit;

@Component
public class PremiumBoughtEventPublisherToRedis extends AbstractEventAggregator<PremiumBoughtEventDto>
        implements EventPublisher {
    private static final String EVENT_TYPE_NAME = "Premium bought";

    public PremiumBoughtEventPublisherToRedis(RedisTemplate<String, Object> redisTemplate,
                                              ObjectMapper javaTimeModuleObjectMapper,
                                              Topic premiumBoughtEventTopic) {
        super(redisTemplate, javaTimeModuleObjectMapper, premiumBoughtEventTopic);
    }

    @Override
    public void publish(Object eventObject) {
        if (eventObject instanceof Premium premium) {
            long userId = premium.getUser().getId();
            int days = (int) ChronoUnit.DAYS.between(premium.getStartDate(), premium.getEndDate());
            double cost = PremiumPeriod.fromDays(days).getCost();

            addToQueue(new PremiumBoughtEventDto(userId, cost, days));
        }
    }

    @Override
    protected String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }

    @Override
    public Class<?> getInstance() {
        return Premium.class;
    }
}
