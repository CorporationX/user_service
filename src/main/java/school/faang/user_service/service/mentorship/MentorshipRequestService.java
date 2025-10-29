package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

/**
 * Сервис для работы с запросами на установление отношений менторства.<br>
 * Предоставляет функциональность для создания новых запросов, фильтрации, принятия и отклонения запросов.
 */
public interface MentorshipRequestService {

    /**
     * Создает новый запрос на установление отношений менторства.
     *
     * @param requestDto Данные для создания запроса.
     * @return Созданный запрос на менторство.
     * @exception ForbiddenException если юзер попытается отправить запрос сам себе
     * или выбранный ментор уже является ментором юзера
     * @exception DataValidationException если последний запрос был менее 3 месяцев назад
     */
    MentorshipRequestDto create(CreateMentorshipRequestDto requestDto);

    /**
     * Возвращает запрос на менторство по его идентификатору.
     *
     * @param requestId идентификатор запроса
     * @return объект запроса в виде DTO
     */
    MentorshipRequestDto toMentorshipRequestDto(long requestId);

    /**
     * Возвращает список запросов на менторство,
     * соответствующих фильтрам по ID юзера, ID ментора и по статусу запроса.
     *
     * @param filter Объект с фильтрами для выбора запросов (ID отправителя, получателя и т.д.).
     * @return Список объектов {@link MentorshipRequestDto}, содержащих запросы на менторство,
     * соответствующие критериям фильтра.
     */
    List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter);

    /**
     * Подтверждает запрос на установление отношений менторства.
     *
     * @param requestId ID запроса, который требуется подтвердить.
     * @exception DataValidationException если запрос не найден
     * @exception ForbiddenException если запрос пытается подтвертить тот, кому не был адресован запрос
     */
    void accept(long requestId);

    /**
     * Отвергает запрос на установление отношений менторства с указанием причины.
     *
     * @param requestId     ID запроса, который требуется отвергнуть.
     * @param rejectionDto  Объект, содержащий причину отказа.
     * @exception DataValidationException если запрос не найден
     * @exception ForbiddenException если запрос пытается отклонить тот, кому не был адресован запрос
     */
    void reject(long requestId, RejectionDto rejectionDto);
}
