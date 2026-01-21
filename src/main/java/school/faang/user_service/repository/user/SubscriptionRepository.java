package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

public interface SubscriptionRepository extends JpaRepository<User, Long> {

    @Query(nativeQuery = true, value = """
            INSERT INTO subscription (follower_id, followee_id) VALUES (:followerId, :followeeId)
            """)
    @Modifying
    void followUser(long followerId, long followeeId);

    @Query(nativeQuery = true, value = """
            DELETE FROM subscription WHERE follower_id = :followerId AND followee_id = :followeeId
            """)
    @Modifying
    void unfollowUser(long followerId, long followeeId);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS(SELECT 1 FROM subscription WHERE follower_id = :followerId AND followee_id = :followeeId)
            """)
    boolean existsByFollowerIdAndFolloweeId(long followerId, long followeeId);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM subscription WHERE followee_id = :followeeId")
    int findFollowersAmountByFolloweeId(long followeeId);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) FROM subscription WHERE follower_id = :followerId")
    int findFolloweesAmountByFollowerId(long followerId);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users AS u
            JOIN subscription AS subs ON u.id = subs.follower_id
            WHERE subs.followee_id = :followeeId
            """)
    Stream<User> findByFolloweeId(long followeeId);

    @Query(nativeQuery = true, value = """
            SELECT u.* FROM users AS u
            JOIN subscription AS subs ON u.id = subs.followee_id
            WHERE subs.follower_id = :followerId
            """)
    Stream<User> findByFollowerId(long followerId);

    @Query(nativeQuery = true, value = """
            SELECT follower_id
            FROM subscription
            WHERE followee_id = :followeeId
            """)
    List<Long> findFollowersIdsByFolloweeId(long followeeId);
}