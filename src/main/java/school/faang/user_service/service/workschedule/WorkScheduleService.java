package school.faang.user_service.service.workschedule;

import school.faang.user_service.dto.workschedule.WorkScheduleDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

/**
 * Сервис для управления рабочими графиками.
 * Предоставляет методы для создания, обновления и получения информации о графиках пользователей.
 */
public interface WorkScheduleService {

    /**
     * Добавляет рабочий график пользователю на основе переданных данных.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь с указанным {@code userId} должен существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Временные параметры должны соответствовать условию:
     *         {@code startTime < startLunch < endLunch < endTime} —
     *         иначе выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param userId идентификатор пользователя, которому необходимо добавить рабочий график.
     * @param workScheduleDto объект {@link WorkScheduleDto}, содержащий информацию о рабочем графике.
     * @return объект {@link WorkScheduleDto}, представляющий добавленный график.
     * @throws EntityNotFoundException если пользователь не найден.
     * @throws DataValidationException если временные параметры не соответствуют требованиям.
     */
    WorkScheduleDto addWorkSchedule(long userId, WorkScheduleDto workScheduleDto);

    /**
     * Обновляет рабочий график у пользователя по его идентификатору.
     * <p>
     * Условия:
     * <ul>
     *     <li>Пользователь с указанным {@code userId} должен существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>График работы с указанным {@code workScheduleId} должен существовать —
     *         иначе выбрасывается {@code EntityNotFoundException}.</li>
     *     <li>Обновление графика работы другого пользователя не допускается —
     *         в этом случае выбрасывается {@code ForbiddenException}.</li>
     *     <li>Временные параметры должны соответствовать условию:
     *         {@code startTime < startLunch < endLunch < endTime} —
     *         иначе выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param userId идентификатор пользователя, чей рабочий график необходимо обновить.
     * @param workScheduleId идентификатор рабочего графика, который необходимо обновить.
     * @param workScheduleDto объект {@link WorkScheduleDto}, содержащий данные об обновлённом графике.
     * @return объект {@link WorkScheduleDto}, представляющий обновленный график.
     * @throws EntityNotFoundException если пользователь или график не найден.
     * @throws ForbiddenException если пользователь пытается обновить чужой график.
     * @throws DataValidationException если временные параметры не соответствуют требованиям.
     */
    WorkScheduleDto updateWorkSchedule(long userId, long workScheduleId, WorkScheduleDto workScheduleDto);

    /**
     * Возвращает информацию о рабочем графике по его идентификатору.
     *
     * @param workScheduleId идентификатор рабочего графика.
     * @return объект {@link WorkScheduleDto}, содержащий данные о рабочем графике.
     * @throws EntityNotFoundException если рабочий график не найден.
     */
    WorkScheduleDto getById(long workScheduleId);
}
