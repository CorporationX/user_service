package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;

    @Transactional
    public void followUser(long followerId, long followeeId) throws DataValidationException {
        validateUserIds(followerId, followeeId);
        validateFollowSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    private void validateFollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already subscribed to user %d.".formatted(followerId, followeeId));
        }
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        validateUserIds(followerId, followeeId);
        validateUnfollowSubscription(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    private void validateUnfollowSubscription(long followerId, long followeeId) throws DataValidationException {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("User %d is already unsubscribe to user %d.".formatted(followerId, followeeId));
        }
    }

    private void validateUserIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("User %s is trying to unsubscribe to himself".formatted(followerId));
        }
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, filter)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }

    private Stream<User> filterUsers(Stream<User> users, UserFilterDto filter) {
        return users.filter(user ->
                user.getUsername().matches(filter.getNamePattern()) && user.getEmail().matches(filter.getEmailPattern()));
    }

    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }
}
