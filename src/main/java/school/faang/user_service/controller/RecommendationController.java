package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationChecker;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private RecommendationChecker recommendationChecker;

    @PostMapping("/create")
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationChecker.validate(recommendation);
        RecommendationDto newRecommendation = recommendationService.create(recommendation);
        return ResponseEntity.ok(newRecommendation);
    }

    @PutMapping("/update")
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody RecommendationUpdateDto toUpdate) {
        RecommendationDto newUpdatedRecommendation = recommendationService.update(toUpdate);
        return ResponseEntity.ok(newUpdatedRecommendation);
    }
}

