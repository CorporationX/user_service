package school.faang.user_service.config.redis.premium;

import school.faang.user_service.dto.premium.PremiumBoughtEventDto;

import java.util.List;

public interface PremiumBoughtEventPublisher {
    void publish(List<PremiumBoughtEventDto> message);
}
