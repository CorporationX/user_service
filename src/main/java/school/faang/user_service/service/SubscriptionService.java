package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.validation.SubscriptionValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final UserMapper userMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionValidator subscriptionValidator;

    @Transactional
    public void followUser(long followerId, long followeeId) {
        subscriptionValidator.validationUsersExists(followerId, followeeId);
        subscriptionValidator.validationSubscriptionExists(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
    }

    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        subscriptionValidator.validationSubscriptionNotExists(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Transactional
    public int getFollowersCount(long followeeId) {
        subscriptionValidator.validationUserExists(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Transactional
    public int getFollowingCount(long followerId) {
        subscriptionValidator.validationUserExists(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filter) {
        subscriptionValidator.validationUserExists(followeeId);
        return subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<UserDto> getFollowing(long followerId, UserFilterDto filter) {
        subscriptionValidator.validationUserExists(followerId);
        return subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
