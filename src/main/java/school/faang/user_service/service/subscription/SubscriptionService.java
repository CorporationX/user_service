package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

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

    public void followUser(long followerId, long followeeId) {
        userValidator.areUsersExist(followerId, followeeId);
        subscriptionValidator.isFollowingExistsValidate(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User with id: {} follow user with id: {}", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        userValidator.areUsersExist(followerId, followeeId);
        subscriptionValidator.isFollowingNotExistsValidate(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User with id: {} unfollow user with id: {}", followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        userValidator.isUserExists(followeeId);

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
        userValidator.isUserExists(followeeId);

        log.info("Getting followers count for user with id {}", followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        userValidator.isUserExists(followerId);

        Stream<User> followings = subscriptionRepository.findByFollowerId(followerId);

        log.info("Getting filtered followings for user with id {}", followerId);
        return filterUsers(filter, followings);
    }

    public int getFollowingCount(long followeeId) {
        userValidator.isUserExists(followeeId);

        log.info("Getting followings count for user with id: {}", followeeId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    public boolean checkFollowerOfFollowee(long followeeId, long followerId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}