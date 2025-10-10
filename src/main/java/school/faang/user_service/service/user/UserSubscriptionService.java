package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponseDto;
import school.faang.user_service.dto.user.UserDto;

import java.util.List;

/**
 * Сервис для управления подписками между пользователями.
 * <p>
 * Предоставляет методы для подписки, отписки, получения количества и списков подписчиков и подписок.
 */
public interface UserSubscriptionService {

    /**
     * Подписывает пользователя на другого пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь не может подписаться сам на себя —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Нельзя подписаться повторно, если подписка уже существует —
     *         в этом случае выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param followerId идентификатор пользователя, который выполняет подписку
     * @param followeeId идентификатор пользователя, на которого оформляется подписка
     */
    void followUser(long followerId, long followeeId);

    /**
     * Отписывает пользователя от другого пользователя.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь не может отписаться сам от себя —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Нельзя отписаться, если подписка отсутствует —
     *         в этом случае выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param followerId идентификатор пользователя, который выполняет отписку
     * @param followeeId идентификатор пользователя, от которого выполняется отписка
     */
    void unfollowUser(long followerId, long followeeId);

    /**
     * Возвращает количество подписчиков пользователя.
     *
     * @param followeeId идентификатор пользователя, для которого необходимо получить количество подписчиков
     * @return объект {@link CountResponseDto}, содержащий количество подписчиков
     */
    CountResponseDto getFollowersCount(long followeeId);

    /**
     * Возвращает количество подписок пользователя.
     *
     * @param followerId идентификатор пользователя, для которого необходимо получить количество подписок
     * @return объект {@link CountResponseDto}, содержащий количество подписок
     */
    CountResponseDto getFolloweesCount(long followerId);

    /**
     * Возвращает список пользователей, которые подписаны на указанного пользователя.
     *
     * @param followeeId идентификатор пользователя, для которого необходимо получить подписчиков
     * @return список объектов {@link UserDto}, представляющих подписчиков
     */
    List<UserDto> getFollowers(long followeeId);

    /**
     * Возвращает список пользователей, на которых подписан указанный пользователь.
     *
     * @param followerId идентификатор пользователя, для которого необходимо получить подписки
     * @return список объектов {@link UserDto}, представляющих пользователей, на которых оформлены подписки
     */
    List<UserDto> getFollowees(long followerId);
}
