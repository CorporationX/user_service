package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.user.UserDto;
import java.util.List;

public interface MentorshipService {
    /**
     * Добавляет связь "менти-ментор"
     * Позволяет менти или ментору добавить связь "менти-ментор"
     * Пользователь не сможет создать такую связь, если она была создана ранее
     * Пользователь не может создать такую связь для самого себя(он не может быть менти/ментором для самого себя)
     *
     * @param mentorId айди ментора(User)
     * @param menteeId айди менти(User)
     */
    void addMentorship(long mentorId, long menteeId);

    /**
     * Позволяет получить менти пользователя по его айди
     *
     * @param userId айди пользователя
     * @return возвращает менти(ДТО-список) пользователя
     */
    List<UserDto> getMentees(long userId);

    /**
     * Позволяет получить менторов пользователя по его айди
     *
     * @param userId айди пользователя
     * @return возвращает менторов(ДТО-список) пользователя
     */
    List<UserDto> getMentors(long userId);

    /**
     * Удаляет связь "менти-ментор"
     * Позволяет менти или ментору удалить связь "менти-ментор"
     * Такая связь удалится только если она была создана ранее
     *
     * @param menteeId айди менти(User)
     * @param mentorId айди ментора(User)
     */
    void deleteMentorship(long menteeId, long mentorId);
}