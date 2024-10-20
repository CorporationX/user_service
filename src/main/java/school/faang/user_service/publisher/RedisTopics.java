package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    RECOMMENDATION_REQUEST_CHANNEL("recommendation_request_channel");

    private final String topicName;

}
