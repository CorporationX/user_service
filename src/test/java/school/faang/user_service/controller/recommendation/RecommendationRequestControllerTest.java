package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {
    private static final int RANDOM_ID = 0;
    private static final String BLANK_STRING = "";
    private static final String ANY_STRING_NOT_BLANK = "any";
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    void testRecommendationRequestIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> recommendationRequestController.requestRecommendation(null));
    }

    @Test
    void testRecommendationRequestIsCreate() {
        recommendationRequestController.requestRecommendation(new RecommendationRequestDto());
        Mockito.verify(recommendationRequestService, Mockito.times(1)).create(Mockito.any());
    }

    @Test
    void testRequestFilterDtoIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> recommendationRequestController.getRecommendationRequests(null));
    }

    @Test
    void testRecommendationRequestServiceCallsGetRequests() {
        recommendationRequestController.getRecommendationRequests(new RequestFilterDto());
        Mockito.verify(recommendationRequestService).getRequests(Mockito.any());
    }

    @Test
    void testRecommendationRequestServiceCallsGetRequest() {
        recommendationRequestController.getRecommendationRequest(Mockito.anyLong());
        Mockito.verify(recommendationRequestService).getRequest(Mockito.anyLong());
    }

    @Test
    void testRejectionIsNull() {
        assertThrows(
                RuntimeException.class,
                () -> recommendationRequestController.rejectRequest(RANDOM_ID, null));
    }

    @Test
    void testRejectionIsEmpty() {
        assertThrows(
                RuntimeException.class,
                () -> recommendationRequestController.rejectRequest(RANDOM_ID, new RejectionDto()));
    }

    @Test
    void testRejectionReasonIsBlank() {
        assertThrows(
                RuntimeException.class,
                () -> recommendationRequestController.rejectRequest(RANDOM_ID, new RejectionDto(BLANK_STRING)));
    }

    @Test
    void testValidRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto(ANY_STRING_NOT_BLANK);
        recommendationRequestController.rejectRequest(RANDOM_ID, rejectionDto);
        Mockito.verify(recommendationRequestService).rejectRequest(RANDOM_ID, rejectionDto);
    }
}