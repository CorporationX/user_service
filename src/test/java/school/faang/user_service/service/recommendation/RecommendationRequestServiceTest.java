package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
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
        Mockito.when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);
        recommendationRequestService.create(recommendationRequestDto);
        Mockito.verify(recommendationRequestService).create(recommendationRequestDto);
    }

//    @Test
//    public void testRecommendationRequestFindOne() {
//        long validId = 8;
//        Mockito.when(recommendationRequestRepository.findById(validId)).thenReturn(Optional.of(recommendationRequest));
//        recommendationRequestService.getRequest(validId);
//        Mockito.verify(recommendationRequestService).getRequest(validId);
//    }

//    @Test
//    public void testRecommendationRequestsFindAll() {
//        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
//    }

    @Test
    public void testRecommendationRequestReject() {
        long id = 8;

        recommendationRequestService.rejectRequest(id, rejectionDto);
        Mockito.verify(recommendationRequestService).rejectRequest(id, rejectionDto);
    }

//    @Test
//    public void testGetRequestThrowEntityNotFound() {
//        long requestId = 1L;
//
//        Mockito.when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.empty());
//        Mockito.when(recommendationRequestService.getRequest(requestId)).thenThrow(EntityNotFoundException.class);
//    }
}
