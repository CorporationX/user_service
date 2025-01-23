package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.OutboxEvent;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.SubscriptionEvent;
import school.faang.user_service.filter.UserFilterEmail;
import school.faang.user_service.filter.UserFilterName;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.outbox.OutboxEventProcessor;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.utils.Helper;
import school.faang.user_service.validator.SubscriptionValidator;
import school.faang.user_service.validator.UserValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionService {
    private static final String AGGREGATE_TYPE = "Follower";
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final SubscriptionValidator subscriptionValidator;
    private final UserValidator userValidator;
    private final UserFilterName userFilterName;
    private final UserFilterEmail userFilterEmail;
    private final UserService userService;
    private final Helper helper;
    private final OutboxEventProcessor outboxEventProcessor;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        userValidator.validateUserById(followerId);
        userValidator.validateUserById(followeeId);
        subscriptionValidator.validateNoSelfSubscription(followerId, followeeId);
        subscriptionValidator.validateSubscriptionCreation(followerId, followeeId);

        subscriptionRepository.followUser(followerId, followeeId);

        LocalDateTime subscribedAt = subscriptionRepository.findCreatedAtByFollowerIdAndFolloweeId(followerId, followeeId);

        SubscriptionEvent subscriptionEvent = SubscriptionEvent.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .subscribedAt(subscribedAt)
                .followerName(userService.getUserContacts(followerId).getUsername())
                .followeeName(userService.getUserContacts(followeeId).getUsername())
                .build();

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(followeeId)
                .aggregateType(AGGREGATE_TYPE)
                .payload(helper.serializeToJson(subscriptionEvent))
                .eventType(SubscriptionEvent.class.getSimpleName())
                .createdAt(subscribedAt)
                .processed(false)
                .build();

        outboxEventProcessor.saveOutboxEvent(outboxEvent);
        log.info("Follower with ID:{} successfully subscribed to followee with ID:{}.", followerId, followeeId);
    }

    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validateSubscriptionRemoval(followerId, followeeId);

        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        userValidator.validateUserById(followeeId);

        List<User> followers = subscriptionRepository.findByFolloweeId(followeeId).toList();
        List<UserDto> followersDto = userMapper.toDto(followers);

        return filterUsers(followersDto, filter);
    }

    public List<UserDto> filterUsers(List<UserDto> users, UserFilterDto filter) {
        Stream<UserDto> userStream = users.stream();

        if (userFilterName.isApplicable(filter)) {
            userStream = userFilterName.apply(userStream, filter);
        }

        if (userFilterEmail.isApplicable(filter)) {
            userStream = userFilterEmail.apply(userStream, filter);
        }

        return userStream.collect(Collectors.toList());
    }

    public long getFollowersCount(long followeeId) {
        userValidator.validateUserById(followeeId);

        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        userValidator.validateUserById(followerId);

        List<User> following = subscriptionRepository.findByFolloweeId(followerId).toList();
        List<UserDto> followingDto = userMapper.toDto(following);

        return filterUsers(followingDto, filter);
    }

    public long getFollowingCount(long followerId) {
        userValidator.validateUserById(followerId);

        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}