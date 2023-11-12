package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequestMapping("api/v1/recommendation")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping("/create")
    public RecommendationDto giveRecommendation(@RequestBody @Validated RecommendationDto recommendation) {
        log.info("Received request to create recommendation for User with ID: {}", recommendation.getReceiverId());
        return recommendationService.create(recommendation);
    }

    @PutMapping("/update")
    public RecommendationDto updateRecommendation(@RequestBody @Validated RecommendationDto recommendation) {
        log.info("Received request to update recommendation for User with ID: {}", recommendation.getReceiverId());
        return recommendationService.update(recommendation);
    }

    @DeleteMapping("/{recommendationId}")
    public void deleteRecommendation(@PathVariable long recommendationId) {
        log.info("Received request to delete Recommendation with ID: {}", recommendationId);
        recommendationService.delete(recommendationId);
    }

    @GetMapping("/receiver/{receiverId}")
    public Page<RecommendationDto> getAllUserRecommendations(@PathVariable long receiverId, @PageableDefault(size = 5) Pageable pageable) {
        log.info("Received request to retrieve user recommendations for user with ID: {}", receiverId);
        return recommendationService.getAllUserRecommendations(receiverId, pageable);
    }

    @GetMapping("/given/{authorId}")
    public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable long authorId, @PageableDefault(size = 5) Pageable pageable) {
        log.info("Received request to retrieve all recommendations given by user with ID: {}", authorId);
        return recommendationService.getAllGivenRecommendations(authorId, pageable);
    }
}