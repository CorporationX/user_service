package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/recommendations")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public RecommendationDto create(@RequestBody RecommendationDto recommendationDto) {
        return recommendationService.create(recommendationDto);
    }
}
