package school.faang.user_service.service.subscription;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.ShortUserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.ShortUserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filters.user.UserFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final ShortUserMapper shortUserMapper;
    private final List<UserFilter> userFilters;

    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Subscribing to yourself. FollowerId = followeeId = %s", followerId));
        }

        boolean existFollowing = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        if (existFollowing) {
            throw new DataValidationException(String.format("Subscribing exist. FollowerId = %s, followeeId = %s ", followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException(String.format("Unsubscribing to yourself. FollowerId = followeeId = %s", followerId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<ShortUserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(users, (stream, filter) -> filter.apply(stream, filterDto), (s1, s2) -> s1)
                .map(shortUserMapper::toDto)
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followeeId);
    }

    public List<ShortUserDto> getFollowing(long followerId, UserFilterDto filterDto) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);

        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(users, (stream, filter) -> filter.apply(stream, filterDto), (s1, s2) -> s1)
                .map(shortUserMapper::toDto)
                .toList();
    }

    public long getFollowingCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

}
