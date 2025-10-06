package school.faang.user_service.service.subscription;

import school.faang.user_service.dto.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;

import java.util.List;

/**
 * Сервис для управления подписками пользователя.<br>
 * Предоставляет методы для подписки и отписки пользователя,
 * а также методы для получения его подписчиков и подписок, включая их количества.
 */
public interface UserSubscriptionService {

    /**
     * Подписывает одного пользователя на другого по переданным ID.<br><br>
     * Пользователь не может подписаться на другого пользователя если он уже подписан.<br>
     * Иначе будет выброшено исключение {@code ForbiddenException}.
     *
     * @param followerId ID пользователя, который подписывается.
     * @param followeeId ID пользователя, на который подписываются.
     */
    void followUser(long followerId, long followeeId);

    /**
     * Отписывает одного пользователя от другого по переданным id.<br><br>
     * Пользователь не может отписаться от другого пользователя если он не был на него подписан.
     * Иначе будет выброшено исключение {@code ForbiddenException}.
     *
     * @param followerId ID пользователя, который подписывается.
     * @param followeeId ID пользователя, на который подписываются.
     */
    void unfollowUser(long followerId, long followeeId);

    /**
     * Возвращает количество подписчиков пользователя по его ID.
     *
     * @param followeeId ID пользователя.
     * @return объект {@code CountResponse} - хранит количество подписчиков пользователя.
     */
    CountResponse getFollowersCount(long followeeId);

    /**
     * Возвращает количество подписок пользователя по его ID.
     *
     * @param followerId ID пользователя.
     * @return объект {@code CountResponse} - хранит количество подписок пользователя.
     */
    CountResponse getFolloweesCount(long followerId);

    /**
     * Возвращает подписчиков пользователя по его ID.
     *
     * @param followeeId ID пользователя.
     * @return объект {@code List<UserDto>} - список подписчиков.
     */
    List<UserDto> getFollowers(long followeeId, UserFiltersDto userFilterDto);

    /**
     * Возвращает подписки пользователя по его ID.
     *
     * @param followeeId ID пользователя.
     * @return объект {@code List<UserDto>} - список подписок (пользователи)
     */
    List<UserDto> getFollowees(long followeeId, UserFiltersDto userFilterDto);
}
