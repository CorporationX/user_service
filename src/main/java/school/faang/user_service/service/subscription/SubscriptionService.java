package school.faang.user_service.service.subscription;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.filter.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    private final List<UserFilter> userFilters;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validationUsersExists(followerId, followeeId);
        validationSubscriptionExists(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validationSubscriptionNotExists(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public int getFollowersCount(long followeeId) {
        validationUserExists(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public int getFollowingCount(long followerId) {
        validationUserExists(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        validationUserExists(followeeId);
        return filterUsers(subscriptionRepository.findByFolloweeId(followeeId), filters);
    }

    @Transactional
    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        validationUserExists(followerId);
        return filterUsers(subscriptionRepository.findByFollowerId(followerId), filters);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(users, (stream, filter) -> filter.apply(stream, filters), Stream::concat)
                .map(userMapper::toDto)
                .toList();
    }

    private void validationSubscriptionExists(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are already subscribed to this user.");
        }
    }

    private void validationSubscriptionNotExists(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("You are not subscribed to this user to unsubscribe from this user");
        }
    }

    private void validationUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("The user does not exist");
        }
    }

    private void validationUsersExists(long followerId, long followeeId) {
        if (!userRepository.existsById(followeeId)) {
            throw new DataValidationException("The user they are trying to subscribe to does not exist");
        }
        if (!userRepository.existsById(followerId)) {
            throw new DataValidationException("The user who is trying to subscribe does not exist");
        }
    }
}
