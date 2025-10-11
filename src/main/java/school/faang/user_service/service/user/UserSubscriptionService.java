package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

/**
 * Сервис для управления подписками между пользователями.
 * <p>
 * Отвечает за операции подписки и отписки, получение количества
 * подписчиков и подписок, а также за выдачу списков пользователей
 * с возможностью фильтрации.
 * </p>
 *
 * <p>Типичные сценарии использования:</p>
 * <ul>
 *   <li>Пользователь подписывается на другого пользователя.</li>
 *   <li>Пользователь отписывается от другого пользователя.</li>
 *   <li>Отображение количества подписчиков или подписок в профиле.</li>
 *   <li>Получение списка подписчиков/подписок с применением фильтров.</li>
 * </ul>
 *
 * <p>Каждый метод должен проверять права доступа и корректность данных
 * на уровне реализации.</p>
 *
 * @author [Gleb Pavlovskyi]
 */

public interface UserSubscriptionService {

    /**
     * Подписывает одного пользователя на другого.
     *
     * @param followerId ID пользователя, который подписывается
     * @param followeeId ID пользователя, на которого оформляется подписка
     * @throws ForbiddenException если пользователь пытается подписать другого пользователя
     * @throws DataValidationException если подписка уже существует или данные некорректны
     */
    void followUser(long followerId, long followeeId);

    /**
     * Отписывает одного пользователя от другого.
     *
     * @param followerId ID пользователя, который хочет отписаться
     * @param followeeId ID пользователя, от которого происходит отписка
     * @throws DataValidationException если подписка отсутствует
     */
    void unfollowUser(long followerId, long followeeId);

    /**
     * Возвращает количество подписчиков указанного пользователя.
     *
     * @param followeeId ID пользователя, подписчиков которого требуется посчитать
     * @return {@link CountResponse} с общим числом подписчиков
     */
    CountResponse getFollowersCount(long followeeId);

    /**
     * Возвращает количество пользователей, на которых подписан указанный пользователь.
     *
     * @param followerId ID пользователя, чьи подписки нужно посчитать
     * @return {@link CountResponse} с общим числом подписок
     */
    CountResponse getFolloweesCount(long followerId);

    /**
     * Возвращает список подписчиков указанного пользователя с возможностью фильтрации.
     *
     * @param followeeId ID пользователя, подписчиков которого нужно получить
     * @param filters необязательный {@link UserFiltersDto} с критериями фильтрации
     * @return список {@link UserDto}, представляющих подписчиков
     */
    List<UserDto> getFollowers(long followeeId, UserFiltersDto filters);

    /**
     * Возвращает список пользователей, на которых подписан указанный пользователь, с возможностью фильтрации.
     *
     * @param followerId ID пользователя, чьи подписки нужно получить
     * @param filters необязательный {@link UserFiltersDto} с критериями фильтрации
     * @return список {@link UserDto}, представляющих подписки
     */
    List<UserDto> getFollowees(long followerId, UserFiltersDto filters);
}
