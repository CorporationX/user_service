package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static reactor.core.publisher.Mono.when;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationRequestServiceTest {
    private final Long userId = 1L;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void getRequestThrowsException() {
        Mockito.when(recommendationRequestRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.getRequest(userId));
    }

    @Test
    void getRequest() {
        Optional<RecommendationRequest> optionalRequest = Optional.of(new RecommendationRequest());
        RecommendationRequest desiredRequest = optionalRequest.get();

        Mockito.when(recommendationRequestRepository.findById(userId))
                .thenReturn(optionalRequest);

        RecommendationRequestDto receiveRequest = recommendationRequestService.getRequest(userId);

        Assert.assertEquals(recommendationRequestMapper.toDto(desiredRequest), receiveRequest);
        Mockito.verify(recommendationRequestRepository).findById(userId);
    }
}
