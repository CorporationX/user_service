package school.faang.user_service.controller.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

@Controller
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @Autowired
    public RecommendationRequestController(RecommendationRequestService recommendationRequestService){
        this.recommendationRequestService = recommendationRequestService;
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequestDto) {
        if (recommendationRequestDto.getMessage() == null || recommendationRequestDto.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }

        Long requesterId = recommendationRequestDto.getRequesterId();
        Long receiverId = recommendationRequestDto.getReceiverId();
        List<String> skills = recommendationRequestDto.getSkills();

        recommendationRequestService.create(requesterId, receiverId, skills);

        return recommendationRequestDto;
    }
}
