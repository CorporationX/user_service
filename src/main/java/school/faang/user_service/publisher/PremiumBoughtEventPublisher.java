package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PremiumBoughtEvent;

@Component
public class PremiumBoughtEventPublisher extends AbstractEventPublisher<PremiumBoughtEvent> {

    @Value("${spring.data.redis.channels.premium_bought_channel.name}")
    private String premiumBoughtChannelName;

    public void publish(PremiumBoughtEvent eventDto) {
        convertAndSend(eventDto, premiumBoughtChannelName);
    }

}
