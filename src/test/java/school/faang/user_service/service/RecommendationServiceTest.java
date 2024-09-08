package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.RecommendationDto;

import static org.junit.jupiter.api.Assertions.*;
class RecommendationServiceTest {
    @Test
    public void testCreateRecommendationSuccess() {
        RecommendationDto recommendation = new RecommendationDto();
        recommendation.setAuthorId(1L);
        recommendation.setReceiverId(2L);
        recommendation.setContent("Great work!");

        RecommendationService service = new RecommendationService();
        assertDoesNotThrow(() -> service.create(recommendation));
    }

}