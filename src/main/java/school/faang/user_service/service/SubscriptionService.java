package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.service.filters.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final FollowerEventPublisher publisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        if (subscriptionExists(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with id %d already follow user with id %d", followerId, followeeId));
        }
        subscriptionRepository.followUser(followerId, followeeId);
        publisher.sendEvent(FollowerEventDto.builder().
                followerId(followerId)
                .followeeId(followeeId)
                .subscriptionTime(LocalDateTime.now())
                .build());
    }

    public void unfollowUser(long followerId, long followeeId) {
        if (!subscriptionExists(followerId, followeeId)) {
            throw new DataValidationException(String.format("User with id %d doesn't follow user with id %d", followerId, followeeId));
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(users, filter);
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);
        return filterUsers(users, filter);
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filters) {
        if (filters == null) {
            return users.map(userMapper::toDto).toList();
        }
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(users, filters))
                .map(userMapper::toDto)
                .toList();
    }

    private boolean subscriptionExists(long followerId, long followeeId) {
        return subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
    }
}