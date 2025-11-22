package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDisplayDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;

import java.util.List;

/**
 * Сервис для управления запросами на менторство.
 * Предоставляет методы для создания, получения, принятия и отклонения запросов на менторство.
 *
 * @author Маляров Максим
 */
public interface MentorshipRequestService {

    /**
     * Создает новый запрос на менторство.
     * Проверяет бизнес-правила: пользователь не может отправить запрос сам себе,
     * не может отправить запрос чаще чем раз в 3 месяца.
     *
     * @param requestDto данные для создания запроса
     * @return созданный запрос в виде DTO
     */
    MentorshipRequestDisplayDto create(CreateMentorshipRequestDto requestDto);

    /**
     * Получает список запросов на менторство с применением фильтров.
     *
     * @param filter фильтры для поиска запросов
     * @return список отфильтрованных запросов
     */
    List<MentorshipRequestDisplayDto> getByFilters(MentorshipRequestFilterDto filter);

    /**
     * Принимает запрос на менторство.
     * Создает связь ментор-менти между пользователями и обновляет статус запроса.
     *
     * @param requestId ID запроса на менторство
     */
    void accept(Long requestId);

    /**
     * Отклоняет запрос на менторство с указанием причины.
     *
     * @param requestId ID запроса на менторство
     * @param rejectionDto данные об отклонении с причиной
     */
    void reject(Long requestId, RejectionDto rejectionDto);
}
