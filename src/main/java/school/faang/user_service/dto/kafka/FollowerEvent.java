package school.faang.user_service.dto.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FollowerEvent(
        @JsonProperty("followerId") Long followerId,
        @JsonProperty("followeeId") Long followeeId,
        @JsonProperty("projectId") Long projectId
) {
}
