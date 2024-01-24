package school.faang.user_service.service.subscription;

public interface SubscriptionService {

    void followUser(long followerId, long followeeId);

    void unfollowUser(long followerId, long followeeId);

    int getFollowingCount(long followerId);

}
