package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.FollowerEvent;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.publisher.SubscriptionPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.SubscriptionService;
import school.faang.user_service.validator.SubscriptionValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final FollowerEventPublisher followerEventPublisher;
    private final SubscriptionPublisher subscriptionPublisher;
    private final SubscriptionValidator validator;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        validator.validateUserIds(followerId, followeeId);
        boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        validator.validateFollowSubscription(exists, followerId, followeeId);

        FollowerEvent followerEvent = FollowerEvent.builder()
                .followeeId(followeeId)
                .eventTime(LocalDateTime.now())
                .build();
        subscriptionPublisher.publish(followerEvent);

        subscriptionRepository.followUser(followerId, followeeId);
        followerEventPublisher.publish(new FollowerEvent(followerId, followeeId, LocalDateTime.now()));
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validator.validateUserIds(followerId, followeeId);
        boolean exists = subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId);
        validator.validateUnfollowSubscription(exists, followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFollowerId(followeeId);
        return filterUsers(followers, filters)
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getFollowing(long followeeId, UserFilterDto filters) {
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return filterUsers(followers, filters)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public int getFollowersCount(long followerId) {
        return subscriptionRepository.findFollowersAmountByFolloweeId(followerId);
    }

    @Override
    public int getFollowingCount(long followerId) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    private Stream<User> filterUsers(Stream<User> followers, UserFilterDto filters) {
        List<UserFilter> applicableFilters = userFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();
        return followers
                .filter(user -> applicableFilters.stream().allMatch(filter -> filter.apply(user, filters)));
    }
}
