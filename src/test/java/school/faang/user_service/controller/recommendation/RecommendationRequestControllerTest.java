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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private Long id;
    private RecommendationRequestDto requestDto;
    private RecommendationRequestDto responseDto = new RecommendationRequestDto();
    private RequestFilterDto filter;
    private List<RecommendationRequestDto> recommendtionRequestDtoList;
    private RejectionDto rejection = new RejectionDto();

    @BeforeEach
    public void setUp() {
        id = 1L;

        rejection = new RejectionDto();
        filter = new RequestFilterDto();
        requestDto = new RecommendationRequestDto();
        responseDto = new RecommendationRequestDto();

        recommendtionRequestDtoList = List.of(new RecommendationRequestDto());

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRequestRecommendation() {
        when(recommendationRequestService.create(any())).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(requestDto);

        assertEquals(responseDto, result);
        verify(recommendationRequestService).create(requestDto);
    }

    @Test
    void testGetRecommendationRequests() {
        when(recommendationRequestService.getRequests(any())).thenReturn(recommendtionRequestDtoList);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(filter);

        assertEquals(recommendtionRequestDtoList, result);
        verify(recommendationRequestService).getRequests(filter);
    }

    @Test
    void testGetRecommendationRequest() {
        when(recommendationRequestService.getRequest(id)).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(id);

        assertEquals(requestDto, result);
        verify(recommendationRequestService).getRequest(id);
    }

    @Test
    void testRejectRequest() {
        when(recommendationRequestService.rejectRequest(id, any())).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(id, rejection);

        assertEquals(responseDto, result);
        verify(recommendationRequestService).rejectRequest(id, rejection);
    }
}