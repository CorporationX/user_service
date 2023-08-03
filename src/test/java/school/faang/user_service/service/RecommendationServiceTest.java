package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void testDelete() {
        Long recommendationId = 1L;
        doNothing().when(recommendationRepository).deleteById(recommendationId);
        recommendationService.delete(recommendationId);
        verify(recommendationRepository, times(1)).deleteById(recommendationId);
    }
}