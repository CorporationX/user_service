package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SubscriptionValidator subscriptionValidator;
    private final UserRepository userRepository;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateExistsUsers(followerId, followeeId);

        if (subscriptionValidator.validateSubscription(followerId, followeeId)) {
            throw new DataValidationException("Такая подписка уже есть");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        followerEventPublisher.publish(new FollowerEvent(followerId, followeeId, LocalDateTime.now()));
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateExistsUsers(followerId, followeeId);

        if (!subscriptionValidator.validateSubscription(followerId, followeeId)) {
            throw new DataValidationException("Такой подписки нет");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followerId, UserFilterDto filters) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followerId).toList();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                followers = filter.apply(followers.stream(), filters).toList();
            }
        }

        return userMapper.toDto(followers);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        List<User> following = subscriptionRepository.findByFollowerId(followeeId).toList();

        for (UserFilter filter : userFilters) {
            if (filter.isApplicable(filters)) {
                following = filter.apply(following.stream(), filters).toList();
            }
        }

        return userMapper.toDto(following);
    }

    @Transactional(readOnly = true)
    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    public void validateExistsUsers(long followerId, long followeeId) {
        if (!userRepository.existsById(followerId) || !userRepository.existsById(followeeId)) {
            throw new DataValidationException("Нет пользователя с таким айди");
        }
    }
}
