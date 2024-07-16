package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.RecommendationRequestService;

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
    private RejectionDto rejection;
    private RecommendationRequestDto messageRequest;
    private RequestFilterDto validRequestFilterDto;

    @BeforeEach
    void setUp() {
        recommendationRequest = new RecommendationRequestDto();
        recommendationRequest.setId(1L);

        validRequestFilterDto = createRequestFilterDtoWithId(RequestStatus.PENDING);
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
    void testRequestRecommendationIsBlank() {
        messageRequest = createRecommendationRequestDtoWithMessage(" ");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestController.requestRecommendation(messageRequest);
        });

        String expectedMessage = "The request contains an empty message";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).create(messageRequest);
    }

    @Test
    void testRequestRecommendationIsNull() {
        messageRequest = createRecommendationRequestDtoWithMessage(null);
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(messageRequest));

        String expectedMessage = "The request contains an empty message";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).create(messageRequest);
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

        when(recommendationRequestService.getRequests(validRequestFilterDto)).thenReturn(expectedRequests);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(validRequestFilterDto).getBody();

        assertNotNull(result);
        assertEquals(expectedRequests.size(), result.size());
        assertEquals(expectedRequests, result);

        verify(recommendationRequestService, times(1)).getRequests(validRequestFilterDto);
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
    private RequestFilterDto createRequestFilterDtoWithId(RequestStatus requestStatus){
        RequestFilterDto result = new RequestFilterDto();
        result.setStatusFilter(requestStatus);
        return result;
    }
    private RejectionDto createRejectionDtoWithReason(String reason){
        RejectionDto result = new RejectionDto();
        result.setReason(reason);
        return result;
    }
}