package school.faang.user_service.service.goal;

import school.faang.user_service.dto.goal.CreateGoalDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalDto;

import java.util.List;

/**
 * Сервис для управления целями пользователя.
 * Предоставляет методы для создания, обновления, удаления и фильтрации целей пользователя.
 */
public interface GoalService {

    /**
     * Создаёт новую цель пользователя.
     * <p>
     * Условия:
     * <ul>
     *      <li>Создать цель может либо ментор для своих менти, либо пользователь сам для себя —
     *      в противном случае выбрасывается {@code ForbiddenException}.</li>
     *      <li>У каждого исполнителя цели не должно быть больше двух активных целей на момент создания новой —
     *      при нарушении выбрасывается {@code DataValidationException}.</li>
     * </ul>
     *
     * @param createGoalDto объект {@link CreateGoalDto}, содержащий информацию для создания цели пользователя
     * @return объект {@link GoalDto}, представляющий созданную цель пользователя
     */
    GoalDto create(CreateGoalDto createGoalDto);

    /**
     * Обновляет цель пользователя.
     * <p>
     * Условия:
     * <ul>
     *      <li>Обновить цель может либо ментор цели, либо участник цели —
     *      в противном случае выбрасывается {@code ForbiddenException}.</li>
     *      <li>Нельзя обновить уже завершенную цель —
     *      при нарушении выбрасывается {@code ForbiddenException}.</li>
     *      <li>Если у цели есть ментор, то завершить цель может только он —
     *      иначе выбрасывается {@code ForbiddenException}.</li>
     * </ul>
     *
     * @param goalId        идентификатор цели пользователя, которую необходимо обновить
     * @param updateGoalDto объект {@link UpdateGoalDto}, содержащий обновлённые данные цели пользователя
     * @return объект {@link GoalDto}, представляющий обновлённую цель пользователя
     */
    GoalDto update(long goalId, UpdateGoalDto updateGoalDto);

    /**
     * Удаляет цель пользователя.
     * <p>
     * Условия:
     * <ul>
     *      <li>Удалить цель может либо ментор цели, либо участник цели —
     *      в противном случае выбрасывается {@code ForbiddenException}.</li>
     *      <li>Если удаляет цель ментор или если в цели всего один участник,
     *      то цель удаляется полностью, а если удаляется один из участников,
     *      при этом остаются другие, то только этот участник удаляется из цели,
     *      а сама цель с другими участниками продолжает существовать.</li>
     * </ul>
     *
     * @param goalId идентификатор цели пользователя, которую необходимо удалить
     */
    void delete(long goalId);

    /**
     * Фильтрует цели пользователя.
     * <p>
     * Условия:
     * <ul>
     *      <li>Отсутствуют.</li>
     * </ul>
     *
     * @param goalFilterDto объект {@link GoalFilterDto}, содержащий параметры, по которым будут
     *                      фильтроваться цели пользователя
     */
    List<GoalDto> getByFilters(GoalFilterDto goalFilterDto);
}
