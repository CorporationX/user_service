package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.exception.subscription.SubscriptionAlreadyExistsException;
import school.faang.user_service.exception.subscription.SubscriptionNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIBE_ITSELF_VALIDATION;
import static school.faang.user_service.exception.ExceptionMessages.SUBSCRIPTION_ALREADY_EXISTS;
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
        subscriptionValidation(followerId, followeeId);
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new SubscriptionAlreadyExistsException(SUBSCRIPTION_ALREADY_EXISTS.getMessage()
                    .formatted(followerId, followeeId));
        }
        validateUsers(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId) == false) {
            throw new SubscriptionNotFoundException(SUBSCRIPTION_NOT_FOUND.getMessage()
                    .formatted(followerId, followeeId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto filters) {
        validateUser(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return userMapper.toSubscriptionUserDtos(filterUsers(followers, filters));
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
        return userMapper.toSubscriptionUserDtos(filterUsers(followees, filters));
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
        if (subscriptionRepository.existsById(userId) == false) {
            throw new EntityNotFoundException(USER_NOT_FOUND.getMessage().formatted(userId));
        }
    }

    private List<User> filterUsers(Stream<User> users, UserFilterDto filters) {
        return userFilters.stream()
                .reduce(users,
                        ((userStream, userFilter) -> userFilter.apply(userStream, filters)),
                        ((userStream, newUserStream) -> newUserStream))
                .toList();
    }

    private void subscriptionValidation(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new ValidationException(SUBSCRIBE_ITSELF_VALIDATION.getMessage());
        }
    }
}
