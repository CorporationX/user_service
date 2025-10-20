package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CountResponse;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFiltersDto;

import java.util.List;

/**
 * Сервис для управления подписками пользователей.
 * Предоставляет функциональность для подписки/отписки,
 * получения списков подписчиков и подписок
 * с возможностью фильтрации по различным критериям.
 */
public interface UserSubscriptionService {

    /**
     * Подписаться на пользователя
     *
     * @param followerId ID пользователя, который подписывается
     * @param followeeId ID пользователя, на которого подписываются
     * @throws school.faang.user_service.exception.DataValidationException
     * если пользователь пытается подписаться на себя
     * @throws school.faang.user_service.exception.DataValidationException
     * если подписка уже существует
     */
    void followUser(long followerId, long followeeId);

    /**
     * Отписаться от пользователя
     *
     * @param followerId ID пользователя, который отписывается
     * @param followeeId ID пользователя, от которого отписываются
     * @throws school.faang.user_service.exception.DataValidationException
     * если пользователь пытается отписаться от себя
     * @throws IllegalArgumentException если подписка не существует
     */
    void unfollowUser(long followerId, long followeeId);

    /**
     * Получить количество подписчиков пользователя
     *
     * @param followeeId ID пользователя, чьих подписчиков подсчитываем
     * @return CountResponse с количеством подписчиков
     */
    CountResponse getFollowersCount(long followeeId);

    /**
     * Получить количество подписок пользователя
     *
     * @param followerId ID пользователя, чьи подписки подсчитываем
     * @return CountResponse с количеством подписок
     */
    CountResponse getFolloweesCount(long followerId);

    /**
     * Получить список подписчиков пользователя с фильтрацией
     *
     * @param followeeId ID пользователя, чьих подписчиков получаем
     * @param filters критерии фильтрации подписчиков (имя, телефон, опыт работы)
     * @return список DTO подписчиков, удовлетворяющих критериям фильтрации
     */
    List<UserDto> getFollowers(long followeeId, UserFiltersDto filters);

    /**
     * Получить список подписок пользователя с фильтрацией
     *
     * @param followerId ID пользователя, чьи подписки получаем
     * @param filters критерии фильтрации подписок (имя, телефон, опыт работы)
     * @return список DTO пользователей, на которых подписан текущий пользователь
     */
    List<UserDto> getFollowees(long followerId, UserFiltersDto filters);
}
