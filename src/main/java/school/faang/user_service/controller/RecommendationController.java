package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/recommendation")
    public class RecommendationController {
        private final RecommendationService recommendationService;
        private final RecommendationValidator recommendationValidator;

        @PostMapping("/")
        public ResponseEntity<RecommendationDto> giveRecommendation(RecommendationDto recommendation) {
            recommendationValidator.validate(recommendation);
            return ResponseEntity.ok(recommendationService.create(recommendation));
        }

        @GetMapping("/receiver/{id}")
        public Page<RecommendationDto> getAllUserRecommendations(@PathVariable(name = "id") Long receiverId, Pageable pageable) {
            return recommendationService.getAllUserRecommendations(receiverId, pageable);
        }

        @GetMapping("/author/{id}")
        public Page<RecommendationDto> getAllGivenRecommendations(@PathVariable(name = "id") Long authorId, Pageable pageable) {
            return recommendationService.getAllGivenRecommendations(authorId, pageable);
        }


    }

