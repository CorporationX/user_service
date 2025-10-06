package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.filter.user.UserFilter;
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
    private final List<UserFilter> userFilters;

    @Override
    public void followUser(long followerId, long followeeId) {
        log.info("User {} пытается подписаться на пользователя {}", followerId, followeeId);
        validateNotSelfAction(followerId, followeeId);
        validateAlreadySubscribed(followerId, followeeId);
        validateUserOwnAction(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User {} успешно подписался на пользователя {}", followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        log.info("User {} пытается отписаться от пользователя {}", followerId, followeeId);
        validateNotSelfAction(followerId, followeeId);
        validateNotSubscribed(followerId, followeeId);
        validateUserOwnAction(followerId, followeeId);
        subscriptionRepository.unfollowUser(followerId, followeeId);
        log.info("User {} успешно отписался от пользователя {}", followerId, followeeId);
    }

    @Override
    public CountResponse getFollowersCount(long followeeId) {
        long count = subscriptionRepository.findFollowersAmountByFolloweeId(followeeId);
        log.info("Получено количество подписчиков пользователя {}: {}", followeeId, count);
        return new CountResponse(count);
    }

    @Override
    public CountResponse getFolloweesCount(long followerId) {
        long count = subscriptionRepository.findFolloweesAmountByFollowerId(followerId);
        log.info("Получено количество подписок пользователя {}: {}", followerId, count);
        return new CountResponse(count);
    }

    @Override
    public List<UserDto> getFollowers(long followeeId, UserFiltersDto filters) {
        log.info("Получение подписчиков пользователя {} с фильтрами {}", followeeId, filters);
        Stream<User> users = subscriptionRepository.findByFolloweeId(followeeId);

        for (UserFilter userFilter : userFilters) {
            users = userFilter.apply(users, filters);
        }

        List<UserDto> followers = users.map(userMapper::toUserDto).toList();

        log.info("Найдено {} подписчиков для пользователя {}", followers.size(), followeeId);
        return followers;
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        log.info("Получение подписок пользователя {} с фильтрами {}", followerId, filters);
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);

        for (UserFilter userFilter : userFilters) {
            users = userFilter.apply(users, filters);
        }

        List<UserDto> followees = users.map(userMapper::toUserDto).toList();

        log.info("Найдено {} подписок для пользователя {}", followees.size(), followerId);
        return followees;
    }

    // ---------------------
    // Методы проверок
    // ---------------------

    private void validateNotSelfAction(long followerId, long followeeId) {
        if (followerId == followeeId) {
            log.warn("User {} пытается подписаться или отписаться от самого себя", followerId);
            throw new DataValidationException("Нельзя подписаться или отписаться от самого себя.");
        }
    }

    private void validateAlreadySubscribed(long followerId, long followeeId) {
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} уже подписан на пользователя {}", followerId, followeeId);
            throw new DataValidationException("Пользователь уже подписан.");
        }
    }

    private void validateNotSubscribed(long followerId, long followeeId) {
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            log.warn("User {} не был подписан на пользователя {}", followerId, followeeId);
            throw new DataValidationException("Пользователь не был подписан.");
        }
    }

    private void validateUserOwnAction(long followerId, long currentUserId) {
        if (followerId != currentUserId) {
            log.warn("User {} пытается подписаться или отписаться от лица пользователя: {}", currentUserId, followerId);
            throw new ForbiddenException("Вы не можете подписывать других пользователей.");
        }
    }
}