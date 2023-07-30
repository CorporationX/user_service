package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;
    @Autowired
    private RecommendationValidator recommendationValidator;

    @PostMapping("/create")
    public ResponseEntity<RecommendationDto> giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationValidator.validate(recommendation);
        return ResponseEntity.ok(recommendationService.create(recommendation));
    }

    @PutMapping("/update")
    public ResponseEntity<RecommendationDto> updateRecommendation(@RequestBody RecommendationDto updated) {
        return ResponseEntity.ok(recommendationService.update(updated));
    }
}

