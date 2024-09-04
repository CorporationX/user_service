package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.subscription.ExistingSubscriptionException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIPTION_ALREADY_EXIST;
import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIPTION_NOT_FOUND;
import static school.faang.user_service.exception.ExceptionMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ExistingSubscriptionException(SUBSCRIPTION_ALREADY_EXIST.getMessage()
                    .formatted(followerId, followeeId));
        }
        validateUsers(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            validateUsers(followerId, followeeId);
            subscriptionRepository.unfollowUser(followerId, followeeId);
        } else {
            throw new SubscriptionNotFoundException(SUBSCRIPTION_NOT_FOUND.getMessage()
                    .formatted(followerId, followeeId));
        }
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto filters) {
        validateUser(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return userMapper.toSubscriptionUserDtoList(filterUsers(followers, filters));
    }

    @Override
    public int getFollowersCount(Long followeeId) {
        validateUser(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowing(Long followerId, UserFilterDto filters) {
        validateUser(followerId);
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return userMapper.toSubscriptionUserDtoList(filterUsers(followees, filters));
    }

    @Override
    public int getFollowingCount(Long followerId) {
        validateUser(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateUsers(Long followerId, Long followeeId) {
        validateUser(followerId);
        validateUser(followeeId);
    }

    private void validateUser(Long userId) {
        if (subscriptionRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(USER_NOT_FOUND.getMessage().formatted(userId));
        }
    }

    private List<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        return userFilters.stream()
                .filter(userFilter -> userFilter.isApplicable(filters))
                .reduce(users,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .toList();
    }
}
