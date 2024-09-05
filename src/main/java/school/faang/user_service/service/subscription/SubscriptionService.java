package school.faang.user_service.service.subscription;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final UserValidator userValidator;

    public void followUser(long followerId, long followeeId) throws ValidationException {
        validateUser(followerId);
        validateUser(followeeId);

        if (checkIfFolloweeAndFollowerIsEquals(followerId, followeeId)) {
            throw new ValidationException("User can't subscribe to himself");
        }

        if (checkIfSubscriptionExists(followerId, followeeId)) {
            throw new ValidationException("Already subscribed");
        }

        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        validateUser(followerId);
        validateUser(followeeId);

        if (checkIfFolloweeAndFollowerIsEquals(followerId, followeeId)) {
            throw new ValidationException("User can't unsubscribe to himself");
        }

        if (!checkIfSubscriptionExists(followerId, followeeId)) {
            throw new ValidationException("Already unsubscribed");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        validateUser(followeeId);
        return userMapper.toDto(filterUsers(subscriptionRepository.findByFollowerId(followeeId), filter).toList());
    }

    public int getFollowersCount(long followeeId) {
        validateUser(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public int getFollowingCount(long followerId) {
        validateUser(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return userMapper.toDto(filterUsers(subscriptionRepository.findByFolloweeId(followeeId), filter).toList());
    }

    private Stream<User> filterUsers(Stream<User> userStream, UserFilterDto filterDto) {
        if (filterDto != null) {
            for (UserFilter userFilter : userFilters) {
                if (userFilter.isApplicable(filterDto)) {
                    userStream = userFilter.apply(userStream, filterDto);
                }
            }
        }
        return userStream;
    }

    private boolean checkIfSubscriptionExists(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    private boolean checkIfFolloweeAndFollowerIsEquals(long followerId, long followeeId) {
        return followerId == followeeId;
    }

    private void validateUser(long userId) {
        userValidator.validateUserId(userId);
        userValidator.validateUserExists(userId);
    }
}
