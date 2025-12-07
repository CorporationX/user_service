package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;

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
     * @param userDto объект {@link CreateUserDto}, содержащий информацию для создания пользователя
     * @return объект {@link UserDto}, представляющий созданного пользователя
     */
    UserDto create(CreateUserDto userDto);

    /**
     * Удаляет пользователя по указанному ID.
     * <p>
     * Условия:
     * <ul>
     *     <li>Запрашиваемый ID должен существовать в БД</li>
     * </ul>
     *
     * @return возвращает ID удаленного пользователя
     */
    Long delete(Long userId);

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
     * @param userId  идентификатор пользователя, чьи данные необходимо обновить
     * @param userDto объект {@link UpdateUserDto}, содержащий обновлённые данные пользователя
     * @return объект {@link UserDto}, представляющий обновлённого пользователя
     */
    UserDto update(long userId, UpdateUserDto userDto);

    /**
     * Обновляет идентификатор диалога с телеграм-ботом
     *
     * @param chatId идентификатор диалога с телеграм-ботом
     * @param email  почта пользователя для аутентификации в телеграм-боте
     * @return объект {@link UserDto}, представляющий обновлённого пользователя
     */
    UserDto updateChatIdByEmail(long chatId, String email); //TODO: изменить параметры на @RequestBody

    /**
     * Возвращает информацию о пользователе по его идентификатору.
     * <p>
     * Если пользователь с указанным идентификатором не найден,
     * выбрасывается {@code EntityNotFoundException}.
     *
     * @param userId идентификатор пользователя
     * @return объект {@link UserDto}, содержащий данные пользователя
     */
    UserDto getUserById(long userId);

    /**
     * Возвращает информацию о пользователях по их идентификатору
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователи с указанными {@code ids} должны существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     * </ul>
     *
     * @param ids идентификаторы пользователей
     * @return объект {@code List<}{@link UserDto}{@code >}, содержащий информацию о пользователях
     */
    List<UserDto> getUsersByIds(List<Long> ids);
}


