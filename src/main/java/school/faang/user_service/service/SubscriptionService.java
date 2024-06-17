package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.validator.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    public final SubscriptionValidator subscriptionValidator;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public int getFollowersCount(long followeeId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validateExistsSubscription(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        addFollowerEvent(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> followersUsers = subscriptionRepository.findByFolloweeId(followeeId);
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(followersUsers, filters))
                .map(userMapper::toDto)
                .toList();
    }

    private void addFollowerEvent(long followerId, long followeeId) {
        FollowerEvent followerEvent = FollowerEvent.builder().followerId(followerId)
                .followeeId(followeeId)
                .followedAt(LocalDateTime.now())
                .build();

        followerEventPublisher.publish(followerEvent);
    }
}
