package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestStatusDto;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @Test
    void shouldReturnCreatedRequestWhenRequestRecommendation() {
        RecommendationRequestDto requestDto = createTestRecommendationRequestDto();
        when(recommendationRequestService.create(requestDto)).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.requestRecommendation(requestDto);

        assertEquals(requestDto, result);
        verify(recommendationRequestService, times(1)).create(requestDto);
    }

    @Test
    void shouldReturnFilteredRequests() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRequesterId(10L);
        filterDto.setReceiverId(20L);
        filterDto.setStatus(new RequestStatusDto("PENDING"));
        RecommendationRequestDto requestDto = createTestRecommendationRequestDto();
        List<RecommendationRequestDto> requests = List.of(requestDto);
        when(recommendationRequestService.getRequests(filterDto)).thenReturn(requests);

        List<RecommendationRequestDto> result = recommendationRequestController.getRecommendationRequests(filterDto);

        assertEquals(1, result.size());
        assertEquals(requestDto, result.get(0));
        verify(recommendationRequestService, times(1)).getRequests(filterDto);
    }

    @Test
    void shouldReturnRequestById() {
        RecommendationRequestDto requestDto = createTestRecommendationRequestDto();
        when(recommendationRequestService.getRequest(1L)).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.getRecommendationRequest(1L);

        assertEquals(requestDto, result);
        verify(recommendationRequestService, times(1)).getRequest(1L);
    }

    @Test
    void shouldReturnUpdatedRequestWhenRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Not enough experience");
        RecommendationRequestDto requestDto = createTestRecommendationRequestDto();
        requestDto.setStatus(new RequestStatusDto("REJECTED"));
        when(recommendationRequestService.rejectRequest(1L, rejectionDto)).thenReturn(requestDto);

        RecommendationRequestDto result = recommendationRequestController.rejectRequest(1L, rejectionDto);

        assertEquals(requestDto, result);
        assertEquals("REJECTED", result.getStatus().getStatus());
        verify(recommendationRequestService, times(1)).rejectRequest(1L, rejectionDto);
    }

    private RecommendationRequestDto createTestRecommendationRequestDto() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setMessage("I need a recommendation");
        requestDto.setSkillIds(List.of(101L, 102L));
        requestDto.setRequesterId(10L);
        requestDto.setReceiverId(20L);
        requestDto.setStatus(new RequestStatusDto("PENDING"));
        return requestDto;
    }
}