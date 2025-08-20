package school.faang.user_service.service.analytics;

import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;

import java.util.List;

/**
 * Сервис для работы с посещениями профилей пользователей.
 * <p>
 * Отвечает за добавление новых записей о посещениях и получение информации о посетителях профиля.
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface ProfileVisitService {
    /**
     * Добавляет запись о посещении профиля пользователем.
     *
     * @param visit DTO с информацией о визите (ID посетителя, ID владельца профиля, время посещения)
     */
    void addVisit(ProfileVisitCreateDto visit);

    /**
     * Получает список визитов к указанному пользователю.
     *
     * @param visitedId ID пользователя, чей профиль посещали
     * @param limit     максимальное количество записей на странице
     * @param page      номер страницы (начиная с 0)
     * @return список DTO с информацией о визитах
     */
    List<ProfileVisitViewDto> getUserVisitors(Long visitedId, int limit, int page);
}
