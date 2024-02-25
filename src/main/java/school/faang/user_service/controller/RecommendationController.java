package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.service.RecommendationService;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;


    @Transactional
    public void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }
}
