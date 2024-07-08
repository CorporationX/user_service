package school.faang.user_service.controller.recommendation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Component
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
@Tag(name = "Операции с рекоммендациями")
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/request")
    @Operation(summary = "Запросить рекомендацию", description = "Возможность запрашивать рекомендацию от другого пользователя")
    public RecommendationRequestDto requestRecommendation(@Valid @RequestBody RecommendationRequestDto recommendationRequest) {
        return recommendationRequestService.create(recommendationRequest);
    }

    @GetMapping("/requests")
    @Operation(summary = "Запросы рекомендаций с фильтрами", description = "Получение списка всех запросов на рекомендацию по определенному фильтру")
    public List<RecommendationRequestDto> getRecommendationRequests(@Valid RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);

    }

    @GetMapping("/requests/{id}")
    @Operation(summary = "Конкретный запрос на рекомендацию", description = "Возможность запрашивать рекомендацию от другого пользователя")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable("id") long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PutMapping("/requests/{id}/reject")
    @Operation(summary = "Отклонить запрос на рекомендацию", description = "Возможность отклонять запрос на рекомендацию, поступивший от другого пользователя")
    public RecommendationRequestDto rejectRequest(@PathVariable("id") long id, @RequestBody RejectionDto rejection) {
        return recommendationRequestService.rejectRequest(id, rejection);
    }
}
