package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void testRequestNotFound() {
        long invalidId = 1236;
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestService.getRequest(invalidId)
        );
    }
    @Test
    public void testRequestFound() {
        long validId = 55;
        recommendationRequestService.getRequest(validId);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).findById(validId);
    }
}
