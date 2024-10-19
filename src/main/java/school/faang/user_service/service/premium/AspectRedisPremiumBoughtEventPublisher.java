package school.faang.user_service.service.premium;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;
import school.faang.user_service.redis.publisher.AbstractEventAggregator;

import java.time.temporal.ChronoUnit;

@Slf4j
@Aspect
@EnableAspectJAutoProxy
@Component
public class AspectRedisPremiumBoughtEventPublisher extends AbstractEventAggregator<PremiumBoughtEventDto> {
    private static final String EVENT_TYPE_NAME = "Premium bought";

    public AspectRedisPremiumBoughtEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                  ObjectMapper javaTimeModuleObjectMapper,
                                                  Topic premiumBoughtEventTopic) {
        super(redisTemplate, javaTimeModuleObjectMapper, premiumBoughtEventTopic);
    }

    @AfterReturning(
            pointcut = "(@annotation(school.faang.user_service.annotation.analytic.send.user.SendPremiumBoughtEvent))",
            returning = "returnValue"
    )
    public void addToPublish(Object returnValue) {
        if (returnValue instanceof Premium premium) {
            long userId = premium.getUser().getId();
            int days = (int) ChronoUnit.DAYS.between(premium.getStartDate(), premium.getEndDate());
            double cost = PremiumPeriod.fromDays(days).getCost();

            addToQueue(new PremiumBoughtEventDto(userId, cost, days));
        } else {
            throw new InvalidReturnTypeException("Method annotated with @SendPremiumBoughtEvent must return Premium");
        }
    }

    @Override
    protected String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }
}
