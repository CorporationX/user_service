package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation_request.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.filter.RecommendationRequestMessageFilter;
import school.faang.user_service.filter.RecommendationRequestStatusFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = RecommendationRequestMapper.INSTANCE;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void getRequestThrowsException() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class,
                () -> recommendationRequestService.getRequest(anyLong()));

        assertEquals("There is no person with such id", illegalStateException.getMessage());
    }

    @Test
    void getRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1);

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto recommendationRequestWithValue = recommendationRequestService.getRequest(1);

        assertNotNull(recommendationRequestWithValue);
        assertEquals(1, recommendationRequestWithValue.getId());
    }

    @Test
    void listToListTest() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(1);

        List<SkillRequest> skills = List.of(skillRequest);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setSkills(skills);
        recommendationRequest.setId(1);
        recommendationRequest.setMessage("msg");

        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.toDto(recommendationRequest);

        assertNotNull(recommendationRequestDto);
        assertEquals(1, recommendationRequestDto.getId());
        assertEquals("msg", recommendationRequestDto.getMessage());
        assertEquals(1, recommendationRequestDto.getSkillsId().get(0));
    }

    @Test
    void testRecommendationRequestMessageFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestMessageFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest2.setMessage("Goodbye");
        recommendationRequest3.setMessage("Hel");

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .messagePattern("Hel")
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);
        assertEquals(2, eventsByFilter.size());
        assertEquals("Hello", eventsByFilter.get(0).getMessage());
        assertEquals("Hel", eventsByFilter.get(1).getMessage());
    }

    @Test
    void testRecommendationRequestStatusFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestStatusFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);
        recommendationRequest2.setStatus(RequestStatus.PENDING);
        recommendationRequest3.setStatus(RequestStatus.PENDING);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .status(RequestStatus.PENDING)
                        .build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(0).getStatus());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(1).getStatus());
    }

}