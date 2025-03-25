package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserFilterMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserFilterMapper userFilterMapper;
    private final UserMapper userMapper;

    public void followUser(long followerId, long targetId) {
        ensureSubscriptionStateValidation(followerId, targetId, false);
        subscriptionRepository.followUser(followerId, targetId);
    }

    public void unfollowUser(long followerId, long targetId) {
        ensureSubscriptionStateValidation(followerId, targetId, true);
        subscriptionRepository.unfollowUser(followerId, targetId);
    }

    private void ensureSubscriptionStateValidation(long followerId, long targetId, boolean shouldExist) {
        if (followerId == targetId) {
            throw new DataValidationException("A user cannot follow themselves. UserId: " + targetId);
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, targetId) != shouldExist) {
            throw new DataValidationException("The subscription has already been issued");
        }
    }

    public List<UserDto> getFollowers(long id, UserFilterDto filterDto) {
        return subscriptionRepository.findByFolloweeId(id)
                .filter(userFilterMapper.toEntity(filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public List<UserDto> getFollowing(long id, UserFilterDto filterDto) {
        return subscriptionRepository.findByFollowerId(id)
                .filter(userFilterMapper.toEntity(filterDto))
                .map(userMapper::toDto)
                .toList();
    }

    public long getFollowersCount(long id) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(id);
    }

    public long getFollowingCount(long id) {
        return subscriptionRepository.findFolloweesAmountByFollowerId(id);
    }
}