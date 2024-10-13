package school.faang.user_service.service.subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.FollowerEvent;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.subscription.filters.UserFiltersApplier;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserFiltersApplier userFilterApplier;
    private final UserMapper userMapper;
    private final SubscriptionValidator validator;
    private final ObjectMapper objectMapper;
    private final FollowerEventPublisher followerEventPublisher;

    @Override
    @Transactional
    public void followUser(Long followerId, Long followeeId) {
        validator.checkIfSubscriptionToHimself(followerId, followeeId);
        validator.checkIfSubscriptionNotExists(followerId, followeeId);
        validator.validateUserIds(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);

        publishFollowerEvent(followerId, followeeId);
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

    private void publishFollowerEvent(Long followerId, Long followeeId) {
        try {
            FollowerEvent followerEvent = new FollowerEvent(followerId, followeeId);
            String message = objectMapper.writeValueAsString(followerEvent);
            followerEventPublisher.publish(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
