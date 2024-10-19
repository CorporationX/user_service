package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    PROFILE_VIEW("profile-view");

    private final String topicName;
}
