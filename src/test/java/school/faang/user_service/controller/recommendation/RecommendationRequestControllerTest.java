package school.faang.user_service.controller.recommendation;

//import org.junit.Assert;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
//import school.faang.user_service.dto.recommendation.RequestFilterDto;
//import school.faang.user_service.service.recommendation.RecommendationRequestService;
//
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//public class RecommendationRequestControllerTest {
//    @Mock
//    private RecommendationRequestService recommendationRequestService;
//
//    @InjectMocks
//    private RecommendationRequestController recommendationRequestController;
//
//    private RecommendationRequestDto recommendationRequestDto;
//
////    @Test
////    public void requestRecommendationEmptyMessageTest() {
////        recommendationRequestDto = new RecommendationRequestDto();
////        recommendationRequestDto.setMessage("");
////        Assert.assertThrows(
////                IllegalArgumentException.class,
////                () -> recommendationRequestController.requestRecommendation(recommendationRequestDto)
////        );
////    }
//
//    @Test
//    public void requestRecommendationCreateStartTest() {
//        recommendationRequestDto = new RecommendationRequestDto();
//        recommendationRequestDto.setMessage("Test Message");
//        recommendationRequestController.requestRecommendation(recommendationRequestDto);
//        Mockito.verify(recommendationRequestService, Mockito.times(1))
//                .create(recommendationRequestDto);
//    }
//
//    @Test
//    public void getRecommendationRequestGetRequestsCallTest() {
//        //List<RecommendationRequestDto> recommendationRequestDtoList = List.of(new RecommendationRequestDto());
//        //Mockito.when(recommendationRequestService.getRequests(Mockito.any()))
//        //        .thenReturn(recommendationRequestDtoList);
//
//        RequestFilterDto filter = new RequestFilterDto();
//        recommendationRequestController.getRecommendationRequests(filter);
//        Mockito.verify(recommendationRequestService, Mockito.times(1))
//                .getRequests(filter);
//    }
//
//    @Test
//    public void getRecommendationRequestGetRequestsReturnTest() {
//        List<RecommendationRequestDto> recommendationRequestDtoList = List.of(new RecommendationRequestDto());
//        Mockito.when(recommendationRequestService.getRequests(Mockito.any()))
//                .thenReturn(recommendationRequestDtoList);
//
//        RequestFilterDto filter = new RequestFilterDto();
////        recommendationRequestController.getRecommendationRequests(filter);
////        Mockito.verify(recommendationRequestService, Mockito.times(1))
////                .getRequests(filter);
//
//        assertEquals(recommendationRequestDtoList, recommendationRequestController.getRecommendationRequests(filter));
//    }
//}

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
    void requestRecommendation() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        RecommendationRequestDto responseDto = new RecommendationRequestDto();
        when(recommendationRequestService.create(any(RecommendationRequestDto.class))).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(requestDto);

        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).create(requestDto);
    }

    @Test
    void getRecommendationRequests() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RecommendationRequestDto> requests = Collections.singletonList(new RecommendationRequestDto());
        when(recommendationRequestService.getRequests(any(RequestFilterDto.class))).thenReturn(requests);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(filter);

        assertEquals(requests, result);
        verify(recommendationRequestService, times(1)).getRequests(filter);
    }

    @Test
    void getRecommendationRequest() {
        long id = 1L;
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        when(recommendationRequestService.getRequest(eq(id))).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(id);

        assertEquals(requestDto, result);
        verify(recommendationRequestService, times(1)).getRequest(id);
    }

    @Test
    void rejectRequest() {
        long id = 1L;
        RejectionDto rejection = new RejectionDto();
        RecommendationRequestDto responseDto = new RecommendationRequestDto();
        when(recommendationRequestService.rejectRequest(eq(id), any(RejectionDto.class))).thenReturn(responseDto);

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(id, rejection);

        assertEquals(responseDto, result);
        verify(recommendationRequestService, times(1)).rejectRequest(id, rejection);
    }
}