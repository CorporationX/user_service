package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.PremiumBoughtEvent;
import school.faang.user_service.publisher.PremiumBoughtEventPublisher;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PremiumService {

    private static final long RANDOM_USER_ID = 1L;
    private static final long RANDOM_PAYMENT_AMOUNT = 1000L;
    private static final long RANDOM_SUBSCRIPTION_DURATION = 30L;
    private final PremiumBoughtEventPublisher publisher;

    public void buyPremium() throws JsonProcessingException {
        PremiumBoughtEvent event = PremiumBoughtEvent.builder()
                .userId(RANDOM_USER_ID)
                .paymentAmount(RANDOM_PAYMENT_AMOUNT)
                .subscriptionDuration(RANDOM_SUBSCRIPTION_DURATION)
                .timestamp(LocalDateTime.now())
                .build();
        publisher.publish(event);
    }
}