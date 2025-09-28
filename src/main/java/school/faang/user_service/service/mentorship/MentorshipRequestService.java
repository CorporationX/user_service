package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

/**
 * Сервис для управления запросами на менторство между пользователями.
 * Отвечает за создание, получение, принятие и отклонение запросов.
 */
public interface MentorshipRequestService {

    /**
     * Создаёт новый запрос на менторство от текущего пользователя к указанному ментору.
     *
     * @param mentorshipRequestDto данные запроса, включая ID ментора и описание
     * @return DTO созданного запроса
     * @throws DataValidationException если пользователь пытается отправить запрос самому себе
     *                                 или если уже существует активный запрос
     */
    MentorshipRequestDto create(CreateMentorshipRequestDto mentorshipRequestDto);

    /**
     * Возвращает список запросов на менторство, отфильтрованных по заданным параметрам.
     *
     * @param filterDto фильтры по отправителю, получателю и статусу
     * @return список подходящих запросов
     * @throws DataValidationException если не указан ни один из параметров фильтрации
     */
    List<MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filterDto);

    /**
     * Принимает запрос на менторство по его ID от имени текущего пользователя.
     * Создаёт менторские отношения между получателем и отправителем запроса.
     *
     * @param requestId идентификатор запроса
     * @throws EntityNotFoundException если запрос не найден
     * @throws DataValidationException если запрос уже обработан или пользователь уже ментор
     * @throws ForbiddenException      если текущий пользователь не является получателем запроса
     */
    void accept(long requestId);

    /**
     * Отклоняет запрос на менторство по его ID от имени текущего пользователя.
     *
     * @param requestId    идентификатор запроса
     * @param rejectionDto причина отказа
     * @throws EntityNotFoundException если запрос не найден
     * @throws DataValidationException если причина отказа пуста или запрос уже обработан
     * @throws ForbiddenException      если текущий пользователь не является получателем запроса
     */
    void reject(long requestId, RejectionDto rejectionDto);

}
