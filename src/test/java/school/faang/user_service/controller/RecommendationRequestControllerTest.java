package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @Test
    public void requestRecommendationSavesAndReturnsTest() {
        RecommendationRequestDto rqdToSave = new RecommendationRequestDto();

        when(recommendationRequestService.create(rqdToSave)).thenReturn(rqdToSave);
        recommendationRequestController.requestRecommendation(rqdToSave);
        verify(recommendationRequestValidator, times(1))
                .isRecommendationRequestMessageNull(rqdToSave);
        verify(recommendationRequestService, times(1))
                .create(rqdToSave);
    }

    @Test
    public void getRecommendationRequestsReturnFilteredListTest() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RecommendationRequestDto> expectedList = Arrays.
                asList(new RecommendationRequestDto(), new RecommendationRequestDto());

        when(recommendationRequestService.getRequests(filter)).thenReturn(expectedList);
        recommendationRequestController.getRecommendationRequests(filter);
        verify(recommendationRequestService, times(1))
                .getRequests(filter);
    }

    @Test
    public void getRecommendationRequestReturnDtoById() {
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setId(1L);

        when(recommendationRequestService.getRequest(rqd.getId())).thenReturn(rqd);
        recommendationRequestController.getRecommendationRequest(rqd.getId());
        verify(recommendationRequestService, times(1))
                .getRequest(rqd.getId());
    }

    @Test
    public void rejectRequestTestReturnDtoTest() {
        RejectionDto rejection = new RejectionDto();
        RecommendationRequestDto rqd = new RecommendationRequestDto();
        rqd.setId(1L);
        rejection.setReason("Not valid");

        when(recommendationRequestService.rejectRequest(rqd.getId(), rejection)).thenReturn(rqd);
        recommendationRequestController.rejectRequest(rqd.getId(), rejection);
        verify(recommendationRequestService, times(1))
                .rejectRequest(rqd.getId(), rejection);
    }
}
