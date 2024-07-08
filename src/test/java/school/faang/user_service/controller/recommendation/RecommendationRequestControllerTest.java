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
    private RecommendationRequestDto validMessageRequest;
    private RecommendationRequestDto blankMessageRequest;
    private RecommendationRequestDto nullMessageRequest;
    private RequestFilterDto validRequestFilterDto;

    @BeforeEach
    void setUp() {
        validMessageRequest = new RecommendationRequestDto();
        validMessageRequest.setMessage("Все good");

        blankMessageRequest = new RecommendationRequestDto();
        blankMessageRequest.setMessage(" ");

        nullMessageRequest = new RecommendationRequestDto();
        nullMessageRequest.setMessage(null);

        validRequestFilterDto = new RequestFilterDto();
        validRequestFilterDto.setIdPattern(1L);

        recommendationRequest = new RecommendationRequestDto();
        recommendationRequest.setId(1L);

        rejection = new RejectionDto();
        rejection.setReason("Good");

    }

    @Test
    void testRequestRecommendationIsValid() {
        when(recommendationRequestService.create(validMessageRequest)).thenReturn(validMessageRequest);
        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(validMessageRequest);

        assertNotNull(result);
        assertEquals(validMessageRequest.getMessage(), result.getMessage());

        verify(recommendationRequestService, times(1)).create(validMessageRequest);
    }

    @Test
    void testRequestRecommendationIsBlank() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestController.requestRecommendation(blankMessageRequest);
        });

        String expectedMessage = "The request contains an empty message";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).create(blankMessageRequest);
    }

    @Test
    void testRequestRecommendationIsNull() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.requestRecommendation(nullMessageRequest));

        String expectedMessage = "The request contains an empty message";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(recommendationRequestService, never()).create(nullMessageRequest);
    }

//    @Test
//    void testGetRecommendationRequestsValid() {
//        List<RecommendationRequestDto> expectedRequests = new ArrayList<>();
//        RecommendationRequestDto expectedOne = new RecommendationRequestDto();
//        RecommendationRequestDto expectedTwo = new RecommendationRequestDto();
//        expectedOne.setId(1L);
//        expectedTwo.setId(2L);
//        expectedRequests.add(expectedOne);
//        expectedRequests.add(expectedTwo);
//
//        when(recommendationRequestService.getRequests(validRequestFilterDto)).thenReturn(expectedRequests);
//
//        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(validRequestFilterDto);
//
//        assertNotNull(result);
//        assertEquals(expectedRequests.size(), result.size());
//        assertEquals(expectedRequests, result);
//
//        verify(recommendationRequestService, times(1)).getRequests(validRequestFilterDto);
//    }

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

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(testId);

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

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(testId, rejection);

        assertNotNull(result);
        assertEquals(recommendationRequest, result);

        verify(recommendationRequestService, times(1)).rejectRequest(testId, rejection);
    }
}