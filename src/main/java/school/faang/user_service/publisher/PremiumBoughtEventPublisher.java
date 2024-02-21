package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.PremiumBoughtEventDto;

@Component
@RequiredArgsConstructor
public class PremiumBoughtEventPublisher extends EventPublisher<PremiumBoughtEventDto> {

    private final ChannelTopic premiumBoughtTopic;

    public void publish(PremiumBoughtEventDto eventDto) {
        convertAndSend(eventDto, premiumBoughtTopic.getTopic());
    }
}
