package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.kafka.FollowerEvent;
import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.kafka.producer.FollowerProducer;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.ProjectSubscriptionRepository;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final FollowerProducer followerProducer;
    private final ProjectSubscriptionRepository projectSubscriptionRepository;

    @Override
    public void followUser(long followerId, long followeeId) {

        followerProducer.sendToKafka(new FollowerEvent(followerId, followeeId, null));
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

    @Override
    public void followProject(long followerId, long projectId) {
        if (!projectSubscriptionRepository.existsByFollowerIdAndProjectId(followerId, projectId)) {
            log.warn("Пользователя с id: {} или проекта с id: {} не существует.", followerId, projectId);
            throw new DataValidationException("Вы не можете подписаться на данный проект.");
        }
        projectSubscriptionRepository.followProject(followerId, projectId);
        followerProducer.sendToKafka(new FollowerEvent(followerId, null, projectId));
    }
}