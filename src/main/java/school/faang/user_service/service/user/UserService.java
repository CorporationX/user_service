package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserUpdateDto;
import school.faang.user_service.dto.user.UserViewDto;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Предоставляет методы для создания, обновления и получения информации о пользователях.
 */
public interface UserService {

    /**
     * Создаёт нового пользователя на основе переданных данных.
     * <p>
     * Условия:
     * <ul>
     *     <li>Email должен быть уникальным —
     *         в противном случае выбрасывается {@code DataIntegrityViolationException}.</li>
     *     <li>Пароль должен удовлетворять требованиям к длине —
     *         при нарушении выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param userDto объект {@link UserCreateDto}, содержащий информацию для создания пользователя
     * @return объект {@link UserViewDto}, представляющий созданного пользователя
     */
    UserViewDto create(UserCreateDto userDto);

    /**
     * Обновляет информацию о существующем пользователе.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь с указанным {@code userId} должен существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Обновление данных другого пользователя не допускается —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Если обновляется email, он должен быть уникальным —
     *         иначе выбрасывается {@code DataIntegrityViolationException}.</li>
     * </ul>
     *
     * @param userId идентификатор пользователя, чьи данные необходимо обновить
     * @param userDto объект {@link UserUpdateDto}, содержащий обновлённые данные пользователя
     * @return объект {@link UserViewDto}, представляющий обновлённого пользователя
     */
    UserViewDto update(Long userId, UserUpdateDto userDto);

    /**
     * Возвращает информацию о пользователе по его идентификатору.
     * <p>
     * Если пользователь с указанным идентификатором не найден,
     * выбрасывается {@code EntityNotFoundException}.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link UserViewDto}, содержащий данные пользователя
     */
    UserViewDto getById(Long userId);


    /**
     * Метод для получения DTO пользователей по списку идентификаторов пользователей
     *
     * @param userIds список идентификаторов
     * @return список {@link UserViewDto}
     */
    List<UserViewDto> getByIds(List<Long> userIds);

    /**
     * Метод для получения id пользователей (с фиксированным кол-вом)
     *
     * @param limit кол-во пользователей для получения
     * @return список id пользователей
     */
    List<Long> getIdsActiveUsers(int limit);

    /**
     * Метод для получения всех id пользователей
     *
     * @return список всех id пользователей
     */
    List<Long> getAllUsers(Integer limit);
}


