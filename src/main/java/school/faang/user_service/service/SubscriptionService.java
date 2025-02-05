package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.rating.annotation.FollowUser;
import school.faang.user_service.service.rating.annotation.UnFollowUser;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;

    @FollowUser
    @Transactional
    public void followUser(long followerId, long followeeId) {
        checkSameUsers(followerId, followeeId);
        checkExistFollower(followerId, followeeId);
        log.info("Following subscription (%d - %d)".formatted(followerId, followeeId));
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @UnFollowUser
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        checkSameUsers(followerId, followeeId);
        log.info("Unfollowing subscription (%d - %d)".formatted(followerId, followeeId));
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public List<User> getFollowers(long followerId, UserFilterDto filters) {
        Stream<User> allFollowers = subscriptionRepository.findByFollowerId(followerId);
        log.info("Followers by id {} and filters {}", followerId, filters);

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(allFollowers,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .toList();
    }

    public int getFollowersCount(long followeeId) {
        log.info("Get followers count for followeeId {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<User> getFollowing(long followeeId, UserFilterDto filters) {
        Stream<User> followingStream = subscriptionRepository.findByFolloweeId(followeeId);
        log.info("Following by id {} and filters {}", followeeId, filters);

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(followingStream,
                        (stream, filter) -> filter.apply(stream, filters),
                        (s1, s2) -> s1)
                .toList();
    }

    public int getFollowingCount(long followerId) {
        log.info("Get following count for followerId {}", followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void checkSameUsers(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("FollowerId %d and FolloweeId %d cannot be the same");
            throw new DataValidationException(
                    "FollowerId %d and FolloweeId %d cannot be the same".formatted(followerId, followeeId)
            );
        }
    }

    private void checkExistFollower(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(
                    "This subscription (%d - %d) already exists".formatted(followerId, followeeId)
            );
        }
    }
}
