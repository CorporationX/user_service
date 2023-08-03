package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/recommendation")
    public class RecommendationController {
        private final RecommendationService recommendationService;

        @GetMapping("/receiver/{id}")
        public Page<RecommendationDto> getAllUserRecommendations(@PathVariable(name = "id") Long receiverId,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "pageSize") int pageSize) {
            return recommendationService.getAllUserRecommendations(receiverId, page, pageSize);
        }

        @GetMapping("/author/{id}")
        public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable(name = "id") Long authorId,
                                                                  @RequestParam(value = "page") int page,
                                                                  @RequestParam(value = "pageSize") int pageSize) {
            return recommendationService.getAllGivenRecommendations(authorId, page, pageSize);
        }
    }

