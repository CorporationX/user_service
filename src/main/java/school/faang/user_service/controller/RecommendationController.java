package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.List;

@RestController
    public class RecommendationController {
        @Autowired
        private RecommendationService recommendationService;
        @Autowired
        private RecommendationValidator recommendationValidator;

        public ResponseEntity<RecommendationDto> giveRecommendation(RecommendationDto recommendation){
            recommendationValidator.validate(recommendation);
            return ResponseEntity.ok(recommendationService.create(recommendation));
        }

        public Page<RecommendationDto> getAllUserRecommendations(Long receiverId, Pageable pageable) {
            return recommendationService.getAllUserRecommendations(receiverId, pageable);
        }
    }

