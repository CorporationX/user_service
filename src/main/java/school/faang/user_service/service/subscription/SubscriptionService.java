package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.redis.event.UserFollowerEvent;
import school.faang.user_service.redis.publisher.UserFollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.subscription.SubscriptionValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SubscriptionValidator subscriptionValidator;
    private final UserValidator userValidator;
    private final UserService userService;
    private final UserFollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        userValidator.validateUserExistence(userService.existsById(followerId));
        userValidator.validateUserExistence(userService.existsById(followeeId));

        subscriptionValidator.isFollowingExistsValidate(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with id: {} follow user with id: {}", followerId, followeeId);
        followerEventPublisher.publish(new UserFollowerEvent(followerId, followeeId));
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        userValidator.validateUserExistence(userService.existsById(followerId));
        userValidator.validateUserExistence(userService.existsById(followeeId));
        subscriptionValidator.isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with id: {} unfollow user with id: {}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        userValidator.validateUserExistence(userService.existsById(followeeId));

        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);

        log.info("Getting filtered followers for user with id {}", followeeId);
        return filterUsers(filter, followers);
    }

    private List<UserDto> filterUsers(UserFilterDto filter, Stream<User> usersStream) {
        return userMapper.entityStreamToDtoList(
                userFilters.stream()
                        .filter(userFilter -> userFilter.isApplicable(filter))
                        .reduce(usersStream,
                                (users, userFilter) -> userFilter.apply(users, filter),
                                (a, b) -> b));
    }

    public int getFollowersCount(long followeeId) {
        userValidator.validateUserExistence(userService.existsById(followeeId));

        log.info("Getting followers count for user with id {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        userValidator.validateUserExistence(userService.existsById(followerId));

        Stream<User> followings = subscriptionRepository.findByFollowerId(followerId);

        log.info("Getting filtered followings for user with id {}", followerId);
        return filterUsers(filter, followings);
    }

    public int getFollowingCount(long followeeId) {
        userValidator.validateUserExistence(userService.existsById(followeeId));

        log.info("Getting followings count for user with id: {}", followeeId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    public boolean checkFollowerOfFollowee(long followeeId, long followerId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public List<Long> getActiveFollowersBatch(Long authorId, Long lastId, int batchSize) {
        log.info("Fetching follower IDs by followee IDs");
        return subscriptionRepository.findActiveFollowersBatch(authorId, lastId, batchSize);
    }
}