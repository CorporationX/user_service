package school.faang.user_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.RecommendationService;
import school.faang.user_service.validator.RecommendationValidator;

    @RestController
    public class RecommendationController {
        @Autowired
        private RecommendationService recommendationService;
        @Autowired
        private RecommendationValidator recommendationValidator;

        @DeleteMapping("/delete/{id}")
        public ResponseEntity deleteRecommendation(@PathVariable Long id) {
            recommendationService.delete(id);
            return ResponseEntity.ok().build();
        }
    }

