package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filters.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can not follow yourself!");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists!");
        }
        subscriptionRepository.followUser(followerId, followeeId);

    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can not unfollow yourself!");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<SubscriptionUserDto> getFollowers(long followeeId, SubscriptionUserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<SubscriptionUserDto> getFollowing(long followeeId, SubscriptionUserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<SubscriptionUserDto> filterUsers(Stream<User> users, SubscriptionUserFilterDto filters) {

        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));
        return userMapper.toDto(users.toList());
    }

}
