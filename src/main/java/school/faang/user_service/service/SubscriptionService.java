package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static school.faang.user_service.exception.ExceptionMessage.REPEATED_SUBSCRIPTION_EXCEPTION;

@Service
@AllArgsConstructor
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;

    public void followUser(long followerId, long followeeId) {
        if (subscriptionRepo.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(REPEATED_SUBSCRIPTION_EXCEPTION.getMessage());
        }

        subscriptionRepo.followUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") subscribed to user (id=" + followeeId + ").");
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepo.unfollowUser(followerId, followeeId);
        log.info("User + (id=" + followerId + ") canceled subscription to user (id=" + followeeId + ").");
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return filterUsers(subscriptionRepo.findByFolloweeId(followeeId), filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepo.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        return filterUsers(subscriptionRepo.findByFollowerId(followerId), filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepo.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        return users
                .filter(filter::matches)
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .toList();
    }
}

