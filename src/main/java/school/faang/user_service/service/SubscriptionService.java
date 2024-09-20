package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.userFilter.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.validator.SubscriptionServiceValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper userMapper;
    private final SubscriptionServiceValidator subscriptionServiceValidator;
    private final FollowerEventPublisher followerEventPublisher;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);

        sendToRedisPublisher(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionServiceValidator.validateFollowUnfollowUser(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        subscriptionServiceValidator.validateGetFollowers(followeeId, filterDto);

        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return userMapper.toDtoList(userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(followers, filterDto))
                .toList());
    }

    public Integer getFollowersCount(long followeeId) {
        subscriptionServiceValidator.validateExistsById(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followeeId, UserFilterDto filterDto) {
        subscriptionServiceValidator.validateGetFollowing(followeeId, filterDto);

        List<User> followings = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return userMapper.toDtoList(userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(followings, filterDto))
                .toList());
    }

    public Integer getFollowingCount(long followerId) {
        subscriptionServiceValidator.validateExistsById(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowersIds(long followeeId) {
        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();

        return followers.stream().map(User::getId).toList();
    }

    @Transactional(readOnly = true)
    public List<Long> getFollowingIds(long followeeId) {
        return subscriptionRepository.findByFolloweeId(followeeId).map(User::getId).toList();
    }

    private void sendToRedisPublisher(Long followerId, Long followeeId) {
        FollowerEventDto followerEventDto = FollowerEventDto.builder()
                .subscribedDateTime(LocalDateTime.now())
                .visitorId(followerId)
                .visitedId(followeeId).build();

        followerEventPublisher.publish(followerEventDto);
    }
}
