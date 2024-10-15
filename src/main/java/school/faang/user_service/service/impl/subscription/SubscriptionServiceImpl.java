package school.faang.user_service.service.impl.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.model.event.follower.FollowerEventDto;
import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.subscription.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator validator;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    @Override
    public void followUser(long followerId, long followeeId) {
        validator.isSubscriber(followerId, followeeId, subscriptionRepository);
        subscriptionRepository.followUser(followerId, followeeId);

        FollowerEventDto event = buildEvent(followerId, followeeId);
        followerEventPublisher.publish(event);
        log.info("Subscription event was sent", event);
    }

    @Transactional
    @Override
    public void unfollowUser(long followerId, long followeeId) {
        validator.isNotSubscriber(followerId, followeeId, subscriptionRepository);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        validator.validateId(followeeId);
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users, filters));

        return userMapper.toDto(users.toList());
    }

    @Override
    public int getFollowersCount(long followeeId) {
        validator.validateId(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    public List<UserDto> getFollowing(long followerId, UserFilterDto filters) {
        validator.validateId(followerId);
        List<User> users = subscriptionRepository.findByFollowerId(followerId).toList();
        userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(users.stream(), filters));

        return userMapper.toDto(users);
    }

    @Override
    public int getFollowingCount(long followerId) {
        validator.validateId(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private static FollowerEventDto buildEvent(long followerId, long followeeId) {
        return FollowerEventDto.builder()
                .followeeId(followeeId)
                .followerId(followerId)
                .subscribedAt(LocalDateTime.now())
                .build();
    }
}
