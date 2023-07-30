package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static reactor.core.publisher.Mono.when;

@ExtendWith(value = {MockitoExtension.class})
public class RecommendationRequestServiceTest {
    private final Long userId = 1L;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void getRequestThrowsException() {
        Mockito.when(recommendationRequestRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.getRequest(userId));
    }

    @Test
    void getRequest() {
        Optional<RecommendationRequest> optionalRequest = Optional.of(new RecommendationRequest());
        RecommendationRequest desiredRequest = optionalRequest.get();

        Mockito.when(recommendationRequestRepository.findById(userId))
                .thenReturn(optionalRequest);

        RecommendationRequestDto receiveRequest = recommendationRequestService.getRequest(userId);

        Assert.assertEquals(recommendationRequestMapper.toDto(desiredRequest), receiveRequest);
        Mockito.verify(recommendationRequestRepository).findById(userId);
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
        assertEquals(Optional.of(1), recommendationRequestDto.getId());
        assertEquals("msg", recommendationRequestDto.getMessage());
        assertEquals(Optional.of(1), recommendationRequestDto.getSkillsId().get(0));
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
                        .message("Hel")
                        .build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getFilterRequest(recommendationRequestFilterDto);
        assertEquals(2, eventsByFilter.size());
        //assertEquals("Hello", eventsByFilter.get(0).getMessage());
        //assertEquals("Hel", eventsByFilter.get(1).getMessage());
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

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getFilterRequest(recommendationRequestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(0).getStatus());
        assertEquals(RequestStatus.PENDING, eventsByFilter.get(1).getStatus());
    }


    @Test
    void testRecommendationRequestMessageAndStatusFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestMessageFilter(), new RecommendationRequestStatusFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest1.setStatus(RequestStatus.ACCEPTED);

        recommendationRequest2.setMessage("Goodbye");
        recommendationRequest2.setStatus(RequestStatus.PENDING);

        recommendationRequest3.setMessage("Hel");
        recommendationRequest3.setStatus(RequestStatus.ACCEPTED);

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder()
                        .message("Hel")
                        .status(RequestStatus.ACCEPTED)
                        .build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getFilterRequest(recommendationRequestFilterDto);

        assertEquals(2, eventsByFilter.size());
        assertEquals("Hello", eventsByFilter.get(0).getMessage());
        assertEquals(RequestStatus.ACCEPTED, eventsByFilter.get(0).getStatus());
    }
}
