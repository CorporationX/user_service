package school.faang.user_service.service.user.subscription;

import school.faang.user_service.dto.user.follower.FollowersPage;

public interface FollowReadService {

    FollowersPage getFollowers(long authorId, String cursor, int limit);

    FollowersPage getFollowing(long userId, String cursor, int limit);
}
