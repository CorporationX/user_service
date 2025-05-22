package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private static final String SUBSCRIBED_EXCEPTION_FORM = "Follower with id %d %s subscribed to followee with id %d";
    private static final String USER_EXIST_FORM = "User with id %d isn`t exist";

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId, "has already", true);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateFollowerAndFollowee(followerId, followeeId, "hasn`t", false);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        validateExistsFollower(followeeId);
        Stream<User> followers = subscriptionRepository.findByFollowerId(followeeId);

        for (UserFilter filter : userFilters) {
            if (filter.isAvailable(filterDto)) {
                followers = filter.apply(followers, filterDto);
            }
        }

        return followers.map(userMapper::toDto).toList();
    }

    public List<UserDto> getFollowers(long followeeId) {
        validateExistsFollower(followeeId);
        return subscriptionRepository.findByFolloweeId(followeeId).map(userMapper::toDto).toList();
    }

    public int getFollowersCount(long followeeId) {
        validateExistsFollower(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowee(long followerId) {
        validateExistsFollower(followerId);
        return subscriptionRepository.findByFollowerId(followerId).map(userMapper::toDto).toList();
    }

    private void validateFollowerAndFollowee(long followerId, long followeeId, String subscribed, boolean isSubscribed) {
        validateExistsFollower(followerId);
        validateExistsFollower(followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId) == isSubscribed) {
            throw new DataValidationException(
                    String.format(SUBSCRIBED_EXCEPTION_FORM, followerId, subscribed, followeeId));
        }
    }

    private void validateExistsFollower(long followerId) {
        if (!userService.isExists(followerId)) {
            throw new DataValidationException(String.format(USER_EXIST_FORM, followerId));
        }
    }
}
