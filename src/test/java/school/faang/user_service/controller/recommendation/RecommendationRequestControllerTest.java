package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;
    @Mock
    private RecommendationRequestService recommendationRequestService;
    private static final long RECOMMENDATION_REQUEST_ID_ONE = 1L;
    private static final String NOT_VALID_STRING = "Not valid";

    @Nested
    class ControllerCallsTests {

        @Test
        @DisplayName("Controller calls service.create method one time and returns savedEntity as Dto")
        public void whenControllerCallsServiceSaveThenReturnsDto() {
            RecommendationRequestDto rqdToSave = RecommendationRequestDto.builder().build();

            when(recommendationRequestService.create(rqdToSave)).thenReturn(rqdToSave);
            recommendationRequestController.requestRecommendation(rqdToSave);
            verify(recommendationRequestService).create(rqdToSave);
        }

        @Test
        @DisplayName("Controller calls service.getRequests method one time and returns filtered list")
        public void whenControllerCallsServiceGetRequestsThenReturnsFilteredList() {
            RecommendationRequestFilterDto filter = RecommendationRequestFilterDto.builder().build();

            List<RecommendationRequestDto> expectedList = List.of(RecommendationRequestDto.builder().build(),
                    RecommendationRequestDto.builder().build());

            when(recommendationRequestService.getRequests(filter)).thenReturn(expectedList);
            recommendationRequestController.getRecommendationRequests(filter);
            verify(recommendationRequestService).getRequests(filter);
        }

        @Test
        @DisplayName("Controller calls service.getRequest method one time and returns RR dto")
        public void whenControllerCallsServiceGetRequestThenReturnsDto() {
            RecommendationRequestDto rqd = RecommendationRequestDto.builder()
                    .id(RECOMMENDATION_REQUEST_ID_ONE)
                    .build();

            when(recommendationRequestService.getRequest(rqd.getId())).thenReturn(rqd);
            recommendationRequestController.getRecommendationRequest(rqd.getId());
            verify(recommendationRequestService).getRequest(rqd.getId());
        }

        @Test
        @DisplayName("Controller call service service.rejectRequest method one time and returns RR dto")
        public void whenControllerCallsServiceRejectRequestThenReturnsDto() {
            RecommendationRejectionDto rejection = RecommendationRejectionDto.builder()
                    .reason(NOT_VALID_STRING)
                    .build();
            RecommendationRequestDto rqd = RecommendationRequestDto.builder()
                    .id(RECOMMENDATION_REQUEST_ID_ONE)
                    .build();

            when(recommendationRequestService.rejectRequest(rqd.getId(), rejection)).thenReturn(rqd);
            recommendationRequestController.rejectRequest(rqd.getId(), rejection);
            verify(recommendationRequestService).rejectRequest(rqd.getId(), rejection);
        }
    }
}
