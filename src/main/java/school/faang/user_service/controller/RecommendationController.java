package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor

public class RecommendationController {
    private final RecommendationService recommendationService;

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

