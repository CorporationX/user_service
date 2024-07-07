package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestRecommendation() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        RecommendationRequestDto responseDto = new RecommendationRequestDto();
        when(recommendationRequestService.create(any(RecommendationRequestDto.class))).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(requestDto);

        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).create(requestDto);
    }

    @Test
    void testGetRecommendationRequests() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RecommendationRequestDto> requests = Collections.singletonList(new RecommendationRequestDto());
        when(recommendationRequestService.getRequests(any(RequestFilterDto.class))).thenReturn(requests);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(filter);

        assertEquals(requests, result);
        verify(recommendationRequestService, times(1)).getRequests(filter);
    }

    @Test
    void testGetRecommendationRequest() {
        long id = 1L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        when(recommendationRequestService.getRequest(eq(id))).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(id);

        assertEquals(requestDto, result);
        verify(recommendationRequestService, times(1)).getRequest(id);
    }

    @Test
    void testRejectRequest() {
        long id = 1L;
        RejectionDto rejection = new RejectionDto();
        RecommendationRequestDto responseDto = new RecommendationRequestDto();
        when(recommendationRequestService.rejectRequest(eq(id), any(RejectionDto.class))).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(id, rejection);

        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).rejectRequest(id, rejection);
    }
}