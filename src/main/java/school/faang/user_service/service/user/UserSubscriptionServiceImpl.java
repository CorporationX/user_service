package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        log.info("followUser requested: followerId={}, followeeId={}", followerId, followeeId);

        if (followerId == followeeId) {
            log.warn("followUser forbidden: self-follow");
            throw new ForbiddenException("Self-following is not allowed.");
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("followUser validation failed: already following");
            throw new DataValidationException("You already follow this user.");
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("followUser success: {} -> {}", followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        log.info("unfollowUser requested: followerId={}, followeeId={}", followerId, followeeId);

        if (followerId == followeeId) {
            log.warn("unfollowUser forbidden: self-unfollow");
            throw new ForbiddenException("Self-unfollowing is not allowed.");
        }

        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("unfollowUser validation failed: can't unfollow due to missing following");
            throw new DataValidationException("You Don't follow this user.");
        }

        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("unfollowUser success: {} -> {}", followerId, followeeId);
    }

    @Override
    public CountResponseDto getFollowersCount(long followeeId) {
        int followersCount = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.debug("getFollowersCount: followeeId={}, count={}", followeeId, followersCount);
        return new CountResponseDto(followersCount);
    }

    @Override
    public CountResponseDto getFolloweesCount(long followerId) {
        int followeesCount = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.debug("getFollowersCount: followerId={}, count={}", followerId, followeesCount);
        return new CountResponseDto(followeesCount);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId) {
        Stream<User> stream = subscriptionRepository.findByFolloweeId(followeeId);
        List<UserDto> results = stream.map(userMapper::toUserDto).toList();
        log.debug("getFollowers: followeeId={}, followersCount={}", followeeId, results.size());
        return results;
    }

    @Override
    public List<UserDto> getFollowees(long followerId) {
        Stream<User> stream = subscriptionRepository.findByFollowerId(followerId);
        List<UserDto> results = stream.map(userMapper::toUserDto).toList();
        log.debug("getFollowees: followerId={}, followeesCount={}", followerId, results.size());
        return results;
    }

}