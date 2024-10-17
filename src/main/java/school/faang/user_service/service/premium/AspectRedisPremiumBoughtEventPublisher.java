package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumBoughtEventDto;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.exception.annotation.InvalidReturnTypeException;
import school.faang.user_service.service.publisher.AbstractEventAggregator;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Aspect
@EnableAspectJAutoProxy
@Component
@RequiredArgsConstructor
public class AspectRedisPremiumBoughtEventPublisher extends AbstractEventAggregator<PremiumBoughtEventDto> {
    private static final String EVENT_TYPE_NAME = "Premium bought";

    private final RedisTemplate<String, PremiumBoughtEventDto> premiumBoughtEventDtoRedisTemplate;
    private final ChannelTopic premiumBoughtEventTopic;

    @AfterReturning(
            pointcut = "(@annotation(school.faang.user_service.annotation.analytic.send.user.SendPremiumBoughtAnalyticEvent))",
            returning = "returnValue"
    )
    public void addToPublish(Object returnValue) {
        if (returnValue instanceof Premium premium) {
            long userId = premium.getUser().getId();
            int days = (int) ChronoUnit.DAYS.between(premium.getStartDate(), premium.getEndDate());
            double cost = PremiumPeriod.fromDays(days).getCost();

            addToQueue(new PremiumBoughtEventDto(userId, cost, days));
        } else {
            throw new InvalidReturnTypeException("Method annotated with @SendPremiumBoughtAnalyticEvent must return Premium");
        }
    }

    @Override
    protected void publishEvents(List<PremiumBoughtEventDto> eventsCopy) {
        premiumBoughtEventDtoRedisTemplate.convertAndSend(premiumBoughtEventTopic.getTopic(), eventsCopy);
        log.info("Publish into topic: {}, message: {}", premiumBoughtEventTopic.getTopic(), eventsCopy);
    }

    @Override
    protected String getEventTypeName() {
        return EVENT_TYPE_NAME;
    }
}
