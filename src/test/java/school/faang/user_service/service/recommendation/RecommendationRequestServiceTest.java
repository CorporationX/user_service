package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private RejectionDto rejectionDto;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(2L)
                .receiverId(3L)
                .message("message")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(8L)
                .requester(new User())
                .receiver(new User())
                .message("message 2")
                .build();

        rejectionDto = RejectionDto.builder()
                .reason("message")
                .build();
    }

    @Test
    public void testRecommendationRequestCreated() {
        recommendationRequestService.create(recommendationRequestDto);
        Mockito.verify(recommendationRequestService).create(recommendationRequestDto);
        Mockito.when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);
    }

    @Test
    public void testRecommendationRequestFindOne() {
        long validId = 8;
        recommendationRequestService.getRequest(validId);
        Mockito.verify(recommendationRequestService).getRequest(validId);
        Mockito.when(recommendationRequestRepository.findById(validId)).thenReturn(Optional.of(recommendationRequest));
    }

    @Test
    public void testRecommendationRequestsFindAll() {
        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
    }

    @Test
    public void testRecommendationRequestReject() {
        long id = 8;

        recommendationRequestService.rejectRequest(id, rejectionDto);
        Mockito.verify(recommendationRequestService).rejectRequest(id, rejectionDto);
    }

    @Test
    public void testGetRequestThrowEntityNotFound() {
        long requestId = 1L;

        Mockito.when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());
        Mockito.when(recommendationRequestService.getRequest(requestId)).thenThrow(EntityNotFoundException.class);
    }
}
