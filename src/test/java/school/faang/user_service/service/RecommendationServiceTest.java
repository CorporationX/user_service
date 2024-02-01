package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import school.faang.user_service.repository.recommendation.RecommendationRepository;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testDeleteRecommendation() {
        long id = 1L;
        recommendationService.delete(id);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(id);
    }
}
