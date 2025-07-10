package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;

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
     * @param userDto объект {@link CreateUserDto}, содержащий информацию для создания пользователя
     * @return объект {@link UserDto}, представляющий созданного пользователя
     */
    UserDto create(CreateUserDto userDto);

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
     * @param userDto объект {@link UpdateUserDto}, содержащий обновлённые данные пользователя
     * @return объект {@link UserDto}, представляющий обновлённого пользователя
     */
    UserDto update(long userId, UpdateUserDto userDto);

    /**
     * Возвращает информацию о пользователе по его идентификатору.
     * <p>
     * Если пользователь с указанным идентификатором не найден,
     * выбрасывается {@code EntityNotFoundException}.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link UserDto}, содержащий данные пользователя
     */
    UserDto getById(long userId);
}


