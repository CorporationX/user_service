package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Tag(name = "recommendationRequest_methods")
@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Operation(
            summary = "Запрос рекомендации",
            description = "Создает запрос на рекомендацию на основе переданных данных."
    )
    public RecommendationRequestDto requestRecommendation(@NonNull RecommendationRequestDto requestDto) {
        return recommendationRequestService.create(requestDto);
    }

    @Operation(
            summary = "Получение списка запросов рекомендаций",
            description = "Возвращает список запросов рекомендаций, отфильтрованных по переданным параметрам."
    )
    public List<RecommendationRequestDto> getRecommendationRequests(@NonNull RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }
    @Operation(
            summary = "Получение запроса рекомендации по ID",
            description = "Возвращает запрос рекомендации по его уникальному идентификатору."
    )
    public RecommendationRequestDto getRecommendationRequest(long id) {
        return recommendationRequestService.getRequest(id);
    }

    @Operation(
            summary = "Отклонение запроса рекомендации",
            description = "Отклоняет запрос рекомендации по ID с указанием причины отказа."
    )
    public RecommendationRequestDto rejectRequest(long id, @NonNull RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
