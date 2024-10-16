package school.faang.user_service.service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    SEARCH_APPEARANCE("search-appearance"),
    PROFILE_VIEW("profile-view");

    private final String topicName;
}
