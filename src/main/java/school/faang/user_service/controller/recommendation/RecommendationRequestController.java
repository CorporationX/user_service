package school.faang.user_service.controller.recommendation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

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
}
