package school.faang.user_service.controller.analytics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.analytics.ProfileVisitService;
import school.faang.user_service.service.analytics.SearchAppearanceService;

import java.util.List;

/**
 * AnalyticsController — REST-контроллер для работы с аналитикой пользователей.
 * <p>
 * Отвечает за обработку HTTP-запросов, связанных с аналитическими событиями:
 * </p>
 * <ul>
 *     <li>Получение списка визитов к профилю пользователя;</li>
 *     <li>Получение списка поисковых появлений пользователя.</li>
 * </ul>
 *
 * <p>Контроллер выполняет базовую валидацию параметров пагинации
 * (limit > 0, page >= 0) и делегирует основную бизнес-логику
 * в {@link ProfileVisitService} и {@link SearchAppearanceService}.</p>
 * <p>
 *     Пример запросов:
 * </p>
 * <pre>
 * GET /analytics/users/{visitedId}/visits?limit=10&page=0
 * GET /analytics/users/{searchedId}/search-appearances?limit=20&page=1
 * </pre>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    private final ProfileVisitService visitService;
    private final SearchAppearanceService searchAppearanceService;


    @GetMapping("/users/{visitedId}/visits")
    public ResponseEntity<List<ProfileVisitViewDto>> getProfileVisits(
            @PathVariable Long visitedId,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        validatePagination(limit, page);
        log.info("getProfileVisits limit: {}, page: {}", limit, page);
        var visits = visitService.getUserVisitors(visitedId, limit, page);
        return ResponseEntity.ok(visits);
    }

    @GetMapping("/users/{searchedId}/search-appearances")
    public ResponseEntity<List<SearchAppearanceViewDto>> getSearchAppearances(
            @PathVariable Long searchedId,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        validatePagination(limit, page);
        var result = searchAppearanceService.getUserSearchAppearance(searchedId, limit, page);
        return ResponseEntity.ok(result);
    }

    private void validatePagination(int limit, int page) {
        if (limit <= 0 || page < 0) {
            throw new DataValidationException("Invalid pagination params");
        }
    }
}
