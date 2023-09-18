package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.analytics.SearchAppearanceEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.user_filters.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    private final UserContext userContext;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validate(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            subscriptionRepository.unfollowUser(followerId, followeeId);
        }
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).collect(Collectors.toList());
        return getUsersDtoAfterFiltration(followers, filters);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        List<User> followees = subscriptionRepository.findByFollowerId(followerId).collect(Collectors.toList());
        return getUsersDtoAfterFiltration(followees, filters);
    }

    @Transactional(readOnly = true)
    public long getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validate(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
    }

    public List<UserDto> getUsersDtoAfterFiltration(List<User> users, UserFilterDto filters) {
        userFilters.stream().filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));

        long userId = userContext.getUserId();

        users.forEach(user -> searchAppearanceEventPublisher
                .publish(SearchAppearanceEventDto.builder()
                        .actorId(userId)
                        .receiverId(user.getId())
                        .receivedAt(LocalDateTime.now())
                        .build()));

        return users.stream().map(userMapper::toDto).toList();
    }
}