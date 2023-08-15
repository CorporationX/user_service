package school.faang.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.RecommendationUpdateDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    @Autowired
    private RecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<RecommendationDto> giveRecommendation(@Valid @RequestBody RecommendationDto recommendation) {
        RecommendationDto newRecommendation = recommendationService.create(recommendation);
        return ResponseEntity.ok(newRecommendation);
    }

    @PutMapping
    public ResponseEntity<RecommendationDto> updateRecommendation(@Valid @RequestBody RecommendationUpdateDto toUpdate) {
        RecommendationDto updatedRecommendation = recommendationService.update(toUpdate);
        return ResponseEntity.ok(updatedRecommendation);
    }

    @DeleteMapping
    public ResponseEntity deleteRecommendation(@PathVariable Long id) {
        recommendationService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/receiver/{id}")
    public Page<RecommendationDto> getAllReceiverRecommendations(@PathVariable(name = "id") Long receiverId,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllReceiverRecommendations(receiverId, page, pageSize);
    }

    @GetMapping("/author/{id}")
    public Page<RecommendationDto> getAllAuthorRecommendations(@PathVariable(name = "id") Long authorId,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "pageSize") int pageSize) {
        return recommendationService.getAllAuthorRecommendations(authorId, page, pageSize);
    }
}

