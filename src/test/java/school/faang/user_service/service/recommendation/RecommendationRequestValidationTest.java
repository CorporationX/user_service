package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static reactor.core.publisher.Mono.when;

@SpringBootTest
public class RecommendationRequestValidationTest {
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
    public void testRecommendationRequestMessageInvalid() {
        recommendationRequestDto.setMessage(null);
//        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(MethodArgumentNotValidException.class);
        assertThrows(MethodArgumentNotValidException.class, () -> recommendationRequestService.create(recommendationRequestDto));
    }

    //    @Test
//    public void testRecommendationRequestRejectInvalid() {
//        long id = 12;
//        rejectionDto.setReason("");
//        Assert.assertThrows(
//                MethodArgumentNotValidException.class,
//                () -> recommendationRequestService.rejectRequest(id, rejectionDto)
//        );
//    }
}
