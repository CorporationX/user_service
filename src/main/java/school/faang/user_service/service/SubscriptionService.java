package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.follower.FollowerEventDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.SubscriptionRequirementsException;
import school.faang.user_service.publis.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters = new ArrayList<>();
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        verifyUserExists(followerId);
        verifyUserExists(followeeId);
        validateFollowUnfollow(followerId, followeeId, true);

        subscriptionRepository.followUser(followerId, followeeId);
        User user = userRepository.findById(followeeId).orElseThrow();
        publishToRedisFollowing(user, followerId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        verifyUserExists(followerId);
        verifyUserExists(followeeId);
        validateFollowUnfollow(followerId, followeeId, false);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<User> getFollowers(long followeeId, UserFilterDto filters) {
        verifyUserExists(followeeId);
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);
        return filter(userStream, filters).toList();
    }

    @Transactional(readOnly = true)
    public List<User> getFollowing(long followerId, UserFilterDto filters) {
        verifyUserExists(followerId);
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followerId);
        return filter(userStream, filters).toList();
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        verifyUserExists(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public int getFollowingCount(long followerId) {
        verifyUserExists(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private Stream<User> filter(Stream<User> users, UserFilterDto filters) {
        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                users = filter.apply(users, filters);
            }
        }
        return users;
    }

    private void validateFollowUnfollow(long followerId, long followeeId, boolean isFollow) {
        if (followerId == followeeId) {
            throw new DataValidationException("You cannot subscribe to yourself");
        }

        boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (isFollow && exists) {
            throw new DataValidationException("Already followed");
        } else if (!isFollow && !exists) {
            throw new DataValidationException("You are not following this user");
        }
    }

    public void verifyUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new SubscriptionRequirementsException("User with the ID " + userId + " doesn't exits");
        }
    }

    private void publishToRedisFollowing(User user, long followerId) {
        FollowerEventDto followerEventDto = FollowerEventDto.builder()
                .username(user.getUsername())
                .followerId(followerId)
                .followeeId(user.getId())
                .build();

        followerEventPublisher.publishFollower(followerEventDto);
    }
}
