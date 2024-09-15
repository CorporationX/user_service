package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RequestFilter;
import school.faang.user_service.service.recommendation.filter.impl.RequestSkillsFilter;
import school.faang.user_service.validator.ValidatorForRecommendationRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestMapper requestMapper;
    @Mock
    private RecommendationRequestRepository requestRepository;
    @Mock
    private ValidatorForRecommendationRequest recommendationRequestValidator;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private RequestSkillsFilter requestSkillsFilter;
    private List<RequestFilter> requestFilters;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequestDto requestDto;
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(1L)
                .recieverId(2L)
                .message("Test message")
                .status(RequestStatus.PENDING)
                .skillRequestDtos(Collections.emptyList())
                .build();

        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        requestFilters = List.of(requestSkillsFilter);

        recommendationRequestService = new RecommendationRequestService(
                requestMapper, requestRepository,
                recommendationRequestValidator,
                skillRequestRepository, requestFilters
        );
    }

    @Test
    void testGetRequest() {
        when(requestRepository.findById(requestDto.getId())).thenReturn(Optional.of(recommendationRequest));
        when(requestMapper.toDto(recommendationRequest)).thenReturn(requestDto);

        RecommendationRequestDto foundRequest = recommendationRequestService.getRequest(1L);

        verify(requestRepository, times(1)).findById(anyLong());
        verify(requestMapper, times(1)).toDto(any());

        assertEquals(requestDto, foundRequest);
    }

    @Test
    void testGetRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.getRequest(1L));
    }

    @Test
    void testRejectRequest() {
        requestDto.setStatus(RequestStatus.PENDING);
        when(requestRepository.findById(requestDto.getId())).thenReturn(Optional.of(recommendationRequest));
        when(requestMapper.toDto(recommendationRequest)).thenReturn(requestDto);
        when(requestMapper.toEntity(requestDto)).thenReturn(recommendationRequest);

        RecommendationRequestDto rejectedRequest = recommendationRequestService.rejectRequest(1L, "Reason");
        rejectedRequest.setStatus(recommendationRequest.getStatus());
        rejectedRequest.setRejectionReason(recommendationRequest.getRejectionReason());

        verify(requestMapper, times(2)).toDto(recommendationRequest);
        assertEquals(RequestStatus.REJECTED, rejectedRequest.getStatus());
        assertEquals("Reason", rejectedRequest.getRejectionReason());
    }

    @Test
    void testRejectRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.rejectRequest(1L, "Reason"));
    }

    @Test
    void testGetFilteredRequests() {
        List<RecommendationRequest> requests = List.of(recommendationRequest);
        RequestFilterDto filters = new RequestFilterDto();
        var receiverId = 1L;

        when(requestRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId)).thenReturn(requests);
        when(requestSkillsFilter.apply(requests, filters)).thenReturn(requests.stream());
        when(requestMapper.toDto(recommendationRequest)).thenReturn(requestDto);
        when(requestFilters.get(0).isApplicable(filters)).thenReturn(true);
        when(requestFilters.get(0).apply(requests, filters)).thenReturn(Stream.of(recommendationRequest));

        List<RecommendationRequestDto> requestDtos = recommendationRequestService.getFilteredRequests(receiverId, filters);

        verify(requestRepository, times(1)).findByReceiverIdOrderByCreatedAtDesc(receiverId);
        verify(requestMapper, times(requests.size())).toDto(recommendationRequest);

        assertFalse(requestDtos.isEmpty());
        assertEquals(requestDto, requestDtos.get(0));
    }

    @Test
    void testGetFilteredRequestsInvalidReceiverId() {
        var listRecommendationByFilter =
                recommendationRequestService.getFilteredRequests(-1L, new RequestFilterDto());
        assertEquals(0, listRecommendationByFilter.size());
    }
}