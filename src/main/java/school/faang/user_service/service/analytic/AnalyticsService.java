package school.faang.user_service.service.analytic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.broker_dtos.FollowerEvent;
import school.faang.user_service.controller.SubscriptionController;
import school.faang.user_service.publisher.RedisMessagePublisher;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SubscriptionController subscriptionController;
    private final RedisMessagePublisher publisher;
    private final ObjectMapper mapper;

    public void sendEvent(FollowerEvent event) throws JsonProcessingException {
        String json = mapper.writeValueAsString(event);
        publisher.publish(json);
    }
}
