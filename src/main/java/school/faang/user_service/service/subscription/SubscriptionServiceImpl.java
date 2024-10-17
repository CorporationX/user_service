package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.FollowerEventDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filters.UserFiltersApplier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserFiltersApplier userFilterApplier;
    private final UserMapper userMapper;
    private final SubscriptionValidator validator;
    private final FollowerEventPublisher eventPublisher;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        validator.checkIfSubscriptionToHimself(followerId, followeeId);
        validator.checkIfSubscriptionNotExists(followerId, followeeId);
        validator.validateUserIds(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        FollowerEventDto followerEvent = FollowerEventDto.builder()
                .followerId(followerId)
                .followeeId(followeeId)
                .timestamp(LocalDateTime.now())
                .build();
        eventPublisher.publish(followerEvent);
    }

    @Override
    @Transactional
    public void unfollowUser(Long followerId, Long followeeId) {
        validator.checkIfSubscriptionExists(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowers(Long followeeId, UserFilterDto filters) {
        validator.validateUserIds(followeeId);
        Stream<User> followers = subscriptionRepository.findByFolloweeId(followeeId);
        return userMapper.toSubscriptionUserDtos(userFilterApplier.applyFilters(followers, filters));
    }

    @Override
    public int getFollowersCount(Long followeeId) {
        validator.validateUserIds(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    @Transactional
    public List<SubscriptionUserDto> getFollowings(Long followerId, UserFilterDto filters) {
        validator.validateUserIds(followerId);
        Stream<User> followees = subscriptionRepository.findByFollowerId(followerId);
        return userMapper.toSubscriptionUserDtos(userFilterApplier.applyFilters(followees, filters));
    }

    @Override
    public int getFollowingCounts(Long followerId) {
        validator.validateUserIds(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }
}
