package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.FollowerResponseDto;
import school.faang.user_service.dto.UserFilterRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final FollowerEventPublisher followerEventPublisher;

    public void followUser(Long followerId, Long followeeId) {
        validateIds(followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription to this user already exists.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.debug("User {} successfully followed user {}", followerId, followeeId);

        followerEventPublisher.publish(FollowerEvent.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .timestamp(LocalDateTime.now())
                .build());
        log.debug("FollowerEvent for user {} following user {} has been published to Redis", followerId, followeeId);
    }

    public void unfollowUser(Long followerId, Long followeeId) {
        validateIds(followerId, followeeId);

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("No active subscription to this user.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.debug("User {} successfully unfollowed user {}", followerId, followeeId);
    }

    public List<FollowerResponseDto> getFollowing(Long followeeId, UserFilterRequestDto filter) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                userStream = userFilter.apply(userStream, filter);
            }
        }

        return userStream
                .map(userMapper::userToUserDto)
                .toList();
    }

    public int getFollowingCount(Long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    public List<FollowerResponseDto> getFollowers(Long followeeId, UserFilterRequestDto filter) {
        Stream<User> userStream = subscriptionRepository.findByFolloweeId(followeeId);

        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filter)) {
                userStream = userFilter.apply(userStream, filter);
            }
        }

        return userStream
                .map(userMapper::userToUserDto)
                .toList();
    }

    public int getFollowersCount(Long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    private void validateIds(Long followerId, Long followeeId) {
        if (Objects.equals(followerId, followeeId)) {
            throw new DataValidationException("Cannot perform action on oneself.");
        }
    }
}

