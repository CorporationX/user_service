package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.SubscriptionUserDto;
import school.faang.user_service.dto.SubscriptionUserFilterDto;
import school.faang.user_service.dto.event.SearchAppearanceEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SubscriptionUserMapper;
import school.faang.user_service.publishers.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user_filter.UserFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionUserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    private final UserContext userContext;

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

        List<SubscriptionUserDto> filteredUsers = userMapper.toDto(users.toList());

        List<Long> userIds = filteredUsers.stream().map(SubscriptionUserDto::getId).toList();

        userIds.forEach(userId -> {
            SearchAppearanceEvent event = new SearchAppearanceEvent();
            event.setViewedUserId(userId);
            event.setViewerUserId(userContext.getUserId());
            event.setViewingTime(LocalDateTime.now());
            searchAppearanceEventPublisher.publish(event);
        });

        return filteredUsers;
    }
}
