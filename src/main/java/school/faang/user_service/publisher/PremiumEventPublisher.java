package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.premium.PremiumEvent;

@Component
public class PremiumEventPublisher extends AbstractEventPublisher<PremiumEvent>{
    @Value("${spring.data.redis.channels.premium_channel.name}")
    private String premiumChannelName;

    public void publish(PremiumEvent event) {
        super.publish(event, premiumChannelName);
    }
}
