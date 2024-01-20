package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.verify;

import school.faang.user_service.controller.RecommendationController;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.service.RecommendationService;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    public void deleteCallsDeleteByIdOnRepository() {
        long id = 123L;
        recommendationService.delete(id);
        verify(recommendationRepository).deleteById(id);
    }
}
