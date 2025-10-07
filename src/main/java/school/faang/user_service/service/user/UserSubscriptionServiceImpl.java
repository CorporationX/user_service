package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.DataValidationException;
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
        validateAlreadySubscribed(followerId, followeeId);
        subscriptionRepository.followUser(followerId, followeeId);
        log.info("User {} успешно подписался на пользователя {}", followerId, followeeId);
    }

    @Override
    public void unfollowUser(long followerId, long followeeId) {
        log.info("User {} пытается отписаться от пользователя {}", followerId, followeeId);
        validateNotSubscribed(followerId, followeeId);
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
        log.info("Найдены подписчики для пользователя {}", followeeId);
        return processUserStream(users, filters);
    }

    @Override
    public List<UserDto> getFollowees(long followerId, UserFiltersDto filters) {
        log.info("Получение подписок пользователя {} с фильтрами {}", followerId, filters);
        Stream<User> users = subscriptionRepository.findByFollowerId(followerId);
        log.info("Найдены подписки для пользователя {}", followerId);
        return processUserStream(users, filters);
    }

    // ---------------------
    // Методы проверок
    // ---------------------

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

    private List<UserDto> processUserStream(Stream<User> users, UserFiltersDto filters) {
        for (UserFilter filter : userFilters) {
            users = filter.apply(users, filters);
        }
        return users.map(userMapper::toUserDto).toList();
    }
}