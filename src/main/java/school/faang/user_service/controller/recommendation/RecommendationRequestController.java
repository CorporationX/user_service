package school.faang.user_service.controller.recommendation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    public RecommendationRequestDto requestRecommendation (@NonNull RecommendationRequestDto requestDto) {
        if (requestDto.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не млжет быть пустым");
        }

        return recommendationRequestService.create(requestDto);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(@NonNull RequestFilterDto filter) {
        return recommendationRequestService.getRequests(filter);
    }
}
