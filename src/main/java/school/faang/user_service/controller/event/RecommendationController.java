package school.faang.user_service.controller.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import school.faang.user_service.dto.RecomendationDto;
import school.faang.user_service.service.RecommendationService;

@Controller
@AllArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;


    @PostMapping("/recommendation")
    public RecomendationDto giveRecommendation(@RequestBody RecomendationDto recomendationDto) {
        return recommendationService.giveRecommendation(recomendationDto);
    }

}

