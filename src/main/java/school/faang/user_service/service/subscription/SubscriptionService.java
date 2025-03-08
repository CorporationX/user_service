package school.faang.user_service.service.subscription;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.subscription.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final List<UserFilter> userFilters;
    private final KafkaTemplate<String, FollowerEvent> kafkaTemplate;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateIds(followerId, followeeId);

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Эта подписка уже существует!");
        }

        subscriptionRepository.followUser(followerId, followeeId);

        FollowerEvent event = new FollowerEvent(followerId, followeeId);
        kafkaTemplate.send("follower_events", event);
        log.info("Sent FollowerEvent: {}", event);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) throws DataValidationException {
        validateIds(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        try (Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId)) {
            return filterUsers(users, filter);
        }
    }

    private List<UserDto> filterUsers(Stream<User> users, UserFilterDto filter) {
        List<User> userList = users.toList();
        userFilters.stream()
                .filter(f -> f.isApplicable(filter))
                .forEach(f -> f.apply(users, filter));
        return userList.stream().map(userMapper::toDto).toList();
    }

    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        try (Stream<User> users = subscriptionRepository.findByFollowerId(followerId)) {
            return filterUsers(users, filter);
        }
    }

    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private void validateIds(long followerId, long followeeId) throws DataValidationException {
        if (followerId == followeeId) {
            throw new DataValidationException("Нельзя подписаться или отписаться от самого себя");
        }
    }
}
