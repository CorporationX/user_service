package school.faang.user_service.service.subscription;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filter.UserFilter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> filters;

    public void followUser(long followerId, long followeeId) {
        followValidation(followerId, followeeId, false);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        followValidation(followerId, followeeId, true);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        return filterUser(subscriptionRepository.findByFolloweeId(followeeId).toList(), filter)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<User> filterUser(List<User> users, UserFilterDto userFilterDto) {
        Stream<User> usersStream = users.stream();
        for (UserFilter filter : filters) {
            if (filter.isApplicable(userFilterDto)) {
                usersStream = filter.apply(usersStream, userFilterDto);
            }
        }
        return usersStream.toList();
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filter) {
        return filterUser(subscriptionRepository.findByFollowerId(followeeId).toList(), filter)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getFollowingCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    private void followValidation(long followerId, long followeeId, boolean shouldExist) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe or unsubscribe to yourself");
        }

        boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);

        if (shouldExist && !exists) {
            throw new DataValidationException("Subscription does not exist");
        }
        if (!shouldExist && exists) {
            throw new DataValidationException("Subscription already exists");
        }
    }
}
