package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.exception.ForbiddenException;

import java.util.List;

public interface MentorshipService {

    /**
     * Добавляет связь между ментором и менти
     *
     * @param mentorId id пользователя-ментора
     * @param menteeId id пользователя-менти
     * @throws ForbiddenException если currentUser не является ни ментором, ни менти
     */
    void addMentorship(long mentorId, long menteeId);

    /**
     * Удаляет связь между ментором и менти
     *
     * @param mentorId id ментора
     * @param menteeId id менти
     */
    void deleteMentorship(long mentorId, long menteeId);

    /**
     * Возвращает список всех менти данного пользователя
     *
     * @param userId id пользователя
     * @return список DTO менти
     */
    List<UserDto> getMentees(long userId);

    /**
     * Возвращает список всех менторов пользователя
     *
     * @param userId id пользователя
     * @return список DTO менторов
     */
    List<UserDto> getMentors(long userId);
}