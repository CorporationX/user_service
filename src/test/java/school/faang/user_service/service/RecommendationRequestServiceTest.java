package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    RecommendationRequestRepository repository;
    @Spy
    RecommendationRequestMapperImpl mapper;
    @InjectMocks
    RecommendationRequestService service;


    @Test
    @DisplayName("Positive test for getRecommendationRequest")
    void getRecommendationRequest_positiveTest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        Mockito.when(
                repository.findById(1L))
                .thenReturn(Optional.of(recommendationRequest));
        assertEquals(
                service.getRequest(1L),
                mapper.toDto(recommendationRequest)
        );
    }

    @Test
    @DisplayName("Negative test with non-existent Id for getRecommendationRequest")
    void getRecommendationRequest_negativeTest_nonExistentId() {
        Mockito.when(
                repository.findById(Mockito.anyLong())
        ).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> service.getRequest(20L)
        );

        assertEquals(RecommendationRequestService.NOT_FOUND, exception.getMessage());
    }

}