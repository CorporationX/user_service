package school.faang.user_service.dto.user.follower;

import java.util.List;

public record FollowersPage(
        List<Long> ids,
        String nextCursor
) {
}