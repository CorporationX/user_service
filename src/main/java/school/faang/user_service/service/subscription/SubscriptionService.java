package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionDto;
import school.faang.user_service.dto.userSubscriptionDto.UserSubscriptionFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.filter.user.UserSubscriptionFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserSubscriptionFilter> userSubscriptionFilters;

    @Transactional
    public boolean followUser(long followerId, long followeeId) throws DataValidationException {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException(ExceptionMessages.EXISTING_SUBSCRIPTION);
        }
        subscriptionRepository.followUser(followerId, followeeId);
        return true;
    }

    @Transactional
    public boolean unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
        return true;
    }

    public List<UserSubscriptionDto> getFollowers(long followeeId, UserSubscriptionFilterDto filter) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        return filterUsers(followers, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public List<User> filterUsers(List<User> users, UserSubscriptionFilterDto filter) {
        Stream<User> userStream = users.stream();
        for (UserSubscriptionFilter userFilter : userSubscriptionFilters) {
            if (userFilter.isApplication(filter)) {
                userStream = userFilter.apply(userStream, filter);
            }
        }
        return userStream
                .skip((long) filter.getPage() * filter.getPageSize())
                .limit(filter.getPageSize())
                .toList();
    }

    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserSubscriptionDto> getFollowing(long followerId, UserSubscriptionFilterDto filter) {
        List<User> followings = subscriptionRepository.findByFollowerId(followerId).toList();
        return filterUsers(followings, filter).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
