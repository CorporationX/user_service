package school.faang.user_service.dto.leaderboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserActionDto {
    EVENT_STARTED(10),
    EVENT_JOINED(5),
    DAILY_CHECK_IN(2),
    PROFILE_VIEWED(1),
    GOAL_INVITATION_SENT(4),
    SUBSCRIBED(6),
    COMMENTED(3),
    LIKED(2),
    MESSAGE_SENT(1),
    MENTORSHIP_STARTED(15),
    POST_CREATED(7),
    PROFILE_PICTURE_UPDATED(4),
    FRIEND_REQUEST_SENT(3),
    SHARED_POST(7),
    REACTED_TO_POST(7),

    SPAMMING(-7),
    POST_REPORTED(-5),
    COMMENT_DELETED_BY_MODERATOR(-4),
    ACCOUNT_SUSPENDED(-15);

    private final int rating;
}
