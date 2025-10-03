package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void followUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException(MessageFormat
                    .format("Пользователь под ID: {0} попытался подписаться на самого себя.", followerId));
        }

        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException(MessageFormat
                    .format("Пользователь под ID: {0} уже подписан на ID: {1}.", followerId, followeeId));
        }

        subscriptionRepository.followUser(followerId, followeeId);
        log.info("Вы подписались на пользователя");
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new ForbiddenException(MessageFormat
                    .format("Пользователь под ID: {0} попытался отписаться от самого себя.", followerId));
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new ForbiddenException(
                    MessageFormat
                            .format("Пользователь {0} и так не подписан на пользователя {1}", followerId, followeeId)
            );
        }
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("{} отписался от пользователя под ID: {}", followeeId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(@NotNull long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.info("У пользователя под ID: {}. Количество подписчиков: {}.", followeeId, count);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(@NotNull long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("Количество подписчиков у пользователя {}", count);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(@NotNull long followeeId) {
        log.info("У пользователя под ID: {}. Был получен список подписчиков", followeeId);
        return subscriptionRepository.findByFolloweeId(followeeId).map(userMapper::toUserDto).toList();
    }

    @Override
    public List<UserDto> getFollowees(@NotNull long followerId) {
        log.info("Был получен список подписчиков у пользователя под ID: {}", followerId);
        return subscriptionRepository.findByFollowerId(followerId).map(userMapper::toUserDto).toList();
    }


}
