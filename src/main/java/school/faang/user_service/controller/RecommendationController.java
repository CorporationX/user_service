package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validation.RecommendationValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendation")
public class RecommendationController {
    private final RecommendationService recommendationService;
    private final RecommendationValidator recommendationValidator;

    @PostMapping
    public Long giveRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationValidator.recommendationEmptyValidation(recommendation);
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public ResponseEntity<Void> updateRecommendation(@RequestBody RecommendationDto recommendation) {
        recommendationValidator.recommendationEmptyValidation(recommendation);
        recommendationService.update(recommendation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable Long receiverId,
                                                             @RequestParam(value = "page") int page,
                                                             @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllUserRecommendations(receiverId, page, pageSize);
    }

    @GetMapping("/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable Long authorId,
                                                              @RequestParam(value = "page") int page,
                                                              @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllGivenRecommendations(authorId, page, pageSize);
    }
}
