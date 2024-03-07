package school.faang.user_service.service.subscription;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.follower.FollowerEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.publisher.FollowerEventPublisher;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.service.filter.UserFilterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final FollowerEventPublisher followerEventPublisher;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final UserFilterService userFilter;

    @Override
    @Transactional
    public void followUser(long followerId, long followeeId) {
        validateUserIds(followerId, followeeId);
        validateSubscriptionExist(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        publishFollowerEvent(followerId, followeeId);
    }

    @Override
    @Transactional
    public void unfollowUser(long followerId, long followeeId) {
        validateUserIds(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
    }

    @Override
    @Transactional
    public int getFollowingCount(long followerId) {
        validateUserId(followerId);
        return subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
    }

    @Override
    @Transactional
    public int getFollowersCount(long followeeId) {
        validateUserId(followeeId);
        return subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
    }

    @Override
    @Transactional
    public List<UserDto> getFollowings(long followerId, UserFilterDto filterDto) {
        validateUserId(followerId);
        Stream<UserDto> userDtoStream = subscriptionRepository.findByFollowerId(followerId)
                .map(userMapper::toDto);
        return userFilter.applyFilters(userDtoStream, filterDto).toList();
    }

    @Override
    @Transactional
    public List<UserDto> getFollowers(long followeeId, UserFilterDto filterDto) {
        validateUserId(followeeId);
        Stream<UserDto> userDtoStream = subscriptionRepository.findByFolloweeId(followeeId)
                .map(userMapper::toDto);
        return userFilter.applyFilters(userDtoStream, filterDto).toList();
    }

    private void validateSubscriptionExist(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("This subscription already exists");
        }
    }

    private void validateUserIds(long followerId, long followeeId) {
        validateUserId(followerId);
        validateUserId(followeeId);
        if (followerId == followeeId) {
            throw new DataValidationException("Follower and followee the same user");
        }
    }

    private void validateUserId(long userId) {
        if (userId <= 0) {
            throw new DataValidationException("User identifiers must be positive numbers");
        }
    }

    private void publishFollowerEvent(long followerId, long followeeId) {
        FollowerEventDto followerEventDto = new FollowerEventDto(followerId, followeeId, LocalDateTime.now());
        followerEventPublisher.publish(followerEventDto);
    }

}
