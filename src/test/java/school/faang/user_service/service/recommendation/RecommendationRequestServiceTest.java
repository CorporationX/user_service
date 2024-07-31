package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exeptions.DataValidationException;
import school.faang.user_service.exeptions.NotFoundElement;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RequestFilter;
import school.faang.user_service.service.recommendation.filter.impl.RequestSkillsFilter;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    private RecommendationRequestMapper requestMapper = Mockito.mock(RecommendationRequestMapper.class);

    private RecommendationRequestRepository requestRepository = Mockito.mock(RecommendationRequestRepository.class);

    private ValidatorForRecommendationRequestService recommendationServiceValidator = Mockito.mock(ValidatorForRecommendationRequestService.class);

    private SkillRequestRepository skillRequestRepository = Mockito.mock(SkillRequestRepository.class);

    private RequestSkillsFilter requestSkillsFilter = Mockito.mock(RequestSkillsFilter.class);
    private List<RequestFilter> requestFilters = List.of(requestSkillsFilter);

    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequestDto requestDto;
    private RecommendationRequest recommendationRequest;

    @BeforeEach
    void setUp() {
        requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequesterId(1L);
        requestDto.setRecieverId(2L);
        requestDto.setMessage("Test message");
        requestDto.setStatus(RequestStatus.PENDING);
        requestDto.setSkillDtos(Collections.emptyList());

        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        recommendationRequestService = new RecommendationRequestService(
                requestMapper, requestRepository,
                recommendationServiceValidator,
                skillRequestRepository, requestFilters
        );
    }

    @Test
    void testCreate() {
        doNothing().when(recommendationServiceValidator).validatorData(any());
        when(skillRequestRepository.existsById(anyLong())).thenReturn(true);
        when(requestRepository.save(any())).thenReturn(recommendationRequest);
        when(requestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(requestMapper.toDto(any())).thenReturn(requestDto);

        RecommendationRequestDto createdRequest = recommendationRequestService.create(requestDto);

        verify(recommendationServiceValidator, times(1)).validatorData(any());
        verify(skillRequestRepository, times(requestDto.getSkillDtos().size())).existsById(anyLong());
        verify(requestRepository, times(1)).save(any());
        verify(requestMapper, times(1)).toEntity(any());
        verify(requestMapper, times(1)).toDto(any());

        assertEquals(requestDto, createdRequest);
    }

    @Test
    void testGetRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(recommendationRequest));
        when(requestMapper.toDto(any())).thenReturn(requestDto);

        RecommendationRequestDto foundRequest = recommendationRequestService.getRequest(1L);

        verify(requestRepository, times(1)).findById(anyLong());
        verify(requestMapper, times(1)).toDto(any());

        assertEquals(requestDto, foundRequest);
    }

    @Test
    void testGetRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundElement exception = assertThrows(NotFoundElement.class, () -> recommendationRequestService.getRequest(1L));

        assertEquals("Not found RequestRecommendation for id: 1", exception.getMessage());
    }

    @Test
    void testRejectRequest() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(recommendationRequest));
        when(requestMapper.toDto(any())).thenReturn(requestDto);
        when(requestMapper.toEntity(any())).thenReturn(recommendationRequest);
        when(requestRepository.save(any())).thenReturn(recommendationRequest);

        RecommendationRequestDto rejectedRequest = recommendationRequestService.rejectRequest(1L, "Reason");

        verify(requestRepository, times(1)).findById(anyLong());
        verify(requestMapper, times(2)).toDto(any());
        verify(requestMapper, times(1)).toEntity(any());
        verify(requestRepository, times(1)).save(any());

        assertEquals(RequestStatus.REJECTED, rejectedRequest.getStatus());
        assertEquals("Reason", rejectedRequest.getRejectionReason());
    }

    @Test
    void testRejectRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundElement exception = assertThrows(NotFoundElement.class, () -> recommendationRequestService.rejectRequest(1L, "Reason"));

        assertEquals("Not found RequestRecommendation for id: 1", exception.getMessage());
    }

    @Test
    void testGetRequests() {
        List<RecommendationRequest> requests = List.of(recommendationRequest);
        RequestFilterDto filters = new RequestFilterDto();
        var receiverId = 1L;

        when(requestRepository.findAllRecommendationRequestForReceiver(receiverId)).thenReturn(requests);
        when(requestSkillsFilter.apply(requests, filters)).thenReturn(requests.stream());
        when(requestMapper.toDto(recommendationRequest)).thenReturn(requestDto);
        when(requestFilters.get(0).isApplicable(any())).thenReturn(true);
        when(requestFilters.get(0).apply(any(), any())).thenReturn(Stream.of(recommendationRequest));

        List<RecommendationRequestDto> requestDtos = recommendationRequestService.getRequests(receiverId, filters);

        verify(requestRepository, times(1)).findAllRecommendationRequestForReceiver(receiverId);
        verify(requestMapper, times(requests.size())).toDto(recommendationRequest);

        assertFalse(requestDtos.isEmpty());
        assertEquals(requestDto, requestDtos.get(0));
    }

    @Test
    void testGetRequestsInvalidReceiverId() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> recommendationRequestService.getRequests(-1L, new RequestFilterDto()));

        assertEquals("recipientID apply in getRequest method was null or negative id: -1", exception.getMessage());
    }
}