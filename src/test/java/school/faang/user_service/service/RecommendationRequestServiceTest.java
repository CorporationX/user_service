package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

public class RecommendationRequestServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    RecommendationRequestDto recommendationRequest = new RecommendationRequestDto();

    @Test
    public void testRequesterIdNotExist() {
        recommendationRequest.setRequesterId(125L);
        recommendationRequestService.validateUsersExist(recommendationRequest);
        Mockito.verify(userRepository, Mockito.times(1)).existsById(recommendationRequest.getRequesterId());
    }

    @Test
    public void testRequestCreated() {
        recommendationRequest.setRequesterId(135L);
        recommendationRequest.setReceiverId(124L);
        recommendationRequest.setMessage("recommendation");
        recommendationRequestService.create(recommendationRequest);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).create(135L, 124L, "recommendation");
    }
}
