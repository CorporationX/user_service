package school.faang.user_service.controller.recommendationRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRejectionDto;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recommendationRequest.RecommendationRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private RecommendationRejectionDto rejection;
    private RecommendationRequestDto messageRequest;
    private RecommendationRequestFilterDto validRecommendationRequestFilterDto;

    @BeforeEach
    void setUp() {
        recommendationRequest = new RecommendationRequestDto();
        recommendationRequest.setId(1L);

        validRecommendationRequestFilterDto = createRequestFilterDtoWithId(RequestStatus.PENDING);
        rejection = createRejectionDtoWithReason("Good");
    }

    @Test
    void testRequestRecommendationIsValid() {
        messageRequest = createRecommendationRequestDtoWithMessage("Все good");
        when(recommendationRequestService.create(messageRequest)).thenReturn(messageRequest);
        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(messageRequest).getBody();

        assertNotNull(result);
        assertEquals(messageRequest.getMessage(), result.getMessage());

        verify(recommendationRequestService, times(1)).create(messageRequest);
    }

    @Test
    void testGetRecommendationRequestsValid() {
        List<RecommendationRequestDto> expectedRequests = new ArrayList<>();
        RecommendationRequestDto expectedOne = new RecommendationRequestDto();
        RecommendationRequestDto expectedTwo = new RecommendationRequestDto();
        expectedOne.setId(1L);
        expectedTwo.setId(2L);
        expectedRequests.add(expectedOne);
        expectedRequests.add(expectedTwo);

        when(recommendationRequestService.getRequests(validRecommendationRequestFilterDto)).thenReturn(expectedRequests);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(validRecommendationRequestFilterDto).getBody();

        assertNotNull(result);
        assertEquals(expectedRequests.size(), result.size());
        assertEquals(expectedRequests, result);

        verify(recommendationRequestService, times(1)).getRequests(validRecommendationRequestFilterDto);
    }

    @Test
    void testGetRecommendationRequestsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.getRecommendationRequests(null));

        String expectedMessage = "Фильтр пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).getRequests(null);
    }

    @Test
    void testGetRecommendationRequestValid() {
        long testId = 1;

        when(recommendationRequestService.getRequest(testId)).thenReturn(recommendationRequest);

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(testId).getBody();

        assertNotNull(result);
        assertEquals(recommendationRequest, result);

        verify(recommendationRequestService, times(1)).getRequest(testId);
    }

    @Test
    void testGetRecommendationRequestSubzero() {
        long testSubzeroId = -1;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.getRecommendationRequest(testSubzeroId));

        String expectedMessage = "Аргумент не может быть отрицательным числом";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).getRequest(testSubzeroId);
    }

    @Test
    void testRejectRequestNull() {
        long testId = 1;
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(testId, null));

        String expectedMessage = "Аргумент пустой";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).rejectRequest(testId, null);
    }

    @Test
    void testRejectRequestValid() {
        long testId = 1;
        when(recommendationRequestService.rejectRequest(testId, rejection)).thenReturn(recommendationRequest);

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(testId, rejection).getBody();

        assertNotNull(result);
        assertEquals(recommendationRequest, result);

        verify(recommendationRequestService, times(1)).rejectRequest(testId, rejection);
    }
    private RecommendationRequestDto createRecommendationRequestDtoWithMessage(String message){
        RecommendationRequestDto result = new RecommendationRequestDto();
        result.setMessage(message);
        return result;
    }
    private RecommendationRequestFilterDto createRequestFilterDtoWithId(RequestStatus requestStatus){
        RecommendationRequestFilterDto result = new RecommendationRequestFilterDto();
        result.setStatusFilter(requestStatus);
        return result;
    }
    private RecommendationRejectionDto createRejectionDtoWithReason(String reason){
        RecommendationRejectionDto result = new RecommendationRejectionDto();
        result.setReason(reason);
        return result;
    }
}