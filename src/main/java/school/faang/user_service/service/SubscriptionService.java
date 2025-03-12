package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.FollowerResponse;
import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;

    public void followUser(Long followerId, Long followeeId) {
        validateIds(followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Подписка на данного пользователя уже имеется.");
        }
        subscriptionRepository.followUser(followerId, followeeId);
        log.debug("Пользователь: {} успешно подписался на: {}", followerId, followeeId);
    }

    public void unfollowUser(Long followerId, Long followeeId) {
        validateIds(followerId, followeeId);

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Нет активной подписки на пользователя.");
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.debug("Пользователь: {} успешно отписался от: {}", followerId, followeeId);
    }

    public List<FollowerResponse> getFollowing(Long followeeId, UserFilterRequest filter) {
        Stream<User> userStream = subscriptionRepository.findByFollowerId(followeeId);

        if (filter != null) {
            for (UserFilter userFilter : userFilters) {
                if (userFilter.isApplicable(filter)) {
                    userStream = userFilter.apply(userStream, filter);
                }
            }
        }

        return userStream
                .map(userMapper::userToUserDto)
                .toList();
    }

    public int getFollowingCount(Long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    public List<FollowerResponse> getFollowers(Long followeeId, UserFilterRequest filter) {
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
        if (followerId == null || followeeId == null) {
            throw new DataValidationException("Нельзя произвести действие над самим собой.");
        }
    }
}
