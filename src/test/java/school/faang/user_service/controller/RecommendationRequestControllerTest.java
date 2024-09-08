package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RejectionDto rejectionDto;
    private RequestFilterDto filterDto;
    private long id;

    @BeforeEach
    void setup() {
        rejectionDto = new RejectionDto("some reason");
        filterDto = RequestFilterDto.builder().build();
        id = 1L;
    }

    @Test
    void testRequestRecommendationWrongArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(null));
    }

    @Test
    void testRequestRecommendationIsOk() {
        RecommendationRequestDto recommendationRequestDto = RecommendationRequestDto.builder().build();
        when(recommendationRequestService.create(recommendationRequestDto)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto responseDto = recommendationRequestController
                .requestRecommendation(recommendationRequestDto);

        verify(recommendationRequestService, times(1)).create(recommendationRequestDto);

        assertEquals(recommendationRequestDto, responseDto);
    }

    @Test
    void testGetRecommendationRequestsWithEmptyFilter() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.getRecommendationRequests(null));
    }

    @Test
    void testGetRecommendationRequestsOk() {
        recommendationRequestController.getRecommendationRequests(filterDto);

        verify(recommendationRequestService, times(1)).getRequests(filterDto);
    }

    @Test
    void testGetRecommendationRequestByIdOk() {
        recommendationRequestController.getRecommendationRequest(id);

        verify(recommendationRequestService, times(1)).getRequest(Mockito.anyLong());
    }

    @Test
    void testRejectRequestWithNullDto() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(id, null));
    }

    @Test
    void testRejectRequestWithEmptyDto() {
        rejectionDto.setReason("");
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(id, rejectionDto));
    }

    @Test
    void testRejectRequestDtoOk() {
        recommendationRequestController.rejectRequest(id, rejectionDto);
        verify(recommendationRequestService, times(1)).rejectRequest(id, rejectionDto);
    }
}
