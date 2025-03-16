package school.faang.user_service.dto.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserImpactDto {
    EVENT_INVITATION_RECEIVED(5),
    FRIEND_REQUEST_RECEIVED(3),
    MENTIONED_IN_COMMENT(4),
    TAGGED_IN_POST(6),
    FOLLOWED(7),
    MESSAGE_RECEIVED(2),
    POST_SHARED_BY_OTHERS(8),
    COMMENT_RECEIVED(5),
    GET_REACTION_TO_POST(3),
    PROFILE_VISITED(2),
    MENTORSHIP_REQUEST_RECEIVED(10),

    UNFRIENDED(-5),
    BLOCKED(-10),
    REPORTED(-8);

    private final int impactScore;
}
