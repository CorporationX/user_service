package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    SEARCH_APPEARANCE("search-appearance"),
    PROFILE_VIEW("profile-view"),
    RECOMMENDATION_REQUEST_CHANNEL("recommendation-request-channel");


    private final String topicName;
}
