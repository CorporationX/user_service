package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RejectFailException;
import school.faang.user_service.exception.RequestNotFoundException;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.FilterRecommendationRequest;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestCreateAt;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestId;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestMessage;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestUpdateAt;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private List<FilterRecommendationRequest> filterRecommendationRequestList;
    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RequestFilterDto requestFilterDto;
    private RecommendationRequestDto expected;
    private LocalDateTime localDateTime1;
    private LocalDateTime localDateTime2;
    private List<RecommendationRequest> recommendationRequests;
    private List<FilterRecommendationRequest> filterRecommendationRequests;
    private RejectionDto rejectionDto;
    RecommendationRequest requestForRejected;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
        localDateTime1 = LocalDateTime.now().minusMonths(7);
        localDateTime2 = LocalDateTime.now();
        expected = new RecommendationRequestDto(4L, "message", null, null, null, null, localDateTime1, localDateTime2);
        filterRecommendationRequests = List.of(new FilterRecommendationRequestMessage(), new FilterRecommendationRequestId(), new FilterRecommendationRequestUpdateAt(), new FilterRecommendationRequestCreateAt());

        RecommendationRequest firstRecommendationRequest = new RecommendationRequest();
        firstRecommendationRequest.setId(4L);
        firstRecommendationRequest.setMessage("message");
        firstRecommendationRequest.setCreatedAt(localDateTime1);
        firstRecommendationRequest.setUpdatedAt(localDateTime2);

        RecommendationRequest secondRecommendationRequest = new RecommendationRequest();
        secondRecommendationRequest.setId(6L);
        secondRecommendationRequest.setMessage("Hello");
        secondRecommendationRequest.setCreatedAt(LocalDateTime.now());
        secondRecommendationRequest.setUpdatedAt(LocalDateTime.now().plusMonths(7));

        recommendationRequests = List.of(firstRecommendationRequest, secondRecommendationRequest);

        rejectionDto = new RejectionDto("text reject");
        requestForRejected = new RecommendationRequest();
    }

    @Test
    void testReceiverIsNull() {
        Assert.assertThrows(UserNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 5L, null, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void testRequesterIsNull() {
        Assert.assertThrows(UserNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), null, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void testTimeOutCheckTrue() {
        Assert.assertThrows(RequestTimeOutException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(6).minusDays(1))));
    }

    @Test
    void testSkillsCheck() {
        Assert.assertThrows(SkillsNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", null, 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void testGetRequest() {
        long id = -5L;
        Assert.assertThrows(RequestNotFoundException.class, () -> {
            recommendationRequestService.getRequest(id);
        });
    }

    @Test
    void testFilterId() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.iterator()).thenReturn(filterRecommendationRequests.iterator());
        requestFilterDto.setId(4L);
        recommendationRequestService.getRequest(requestFilterDto);
        verify(recommendationRequestMapper, times(1)).toDto(captor.capture());
        assertTrue(expected.getId() == captor.getValue().getId());
    }

    @Test
    void testFilterMessage() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.iterator()).thenReturn(filterRecommendationRequests.iterator());
        requestFilterDto.setMessage("message");
        recommendationRequestService.getRequest(requestFilterDto);
        verify(recommendationRequestMapper, times(1)).toDto(captor.capture());
        assertEquals(expected.getMessage(), captor.getValue().getMessage());
    }

    @Test
    void testFilterCreateAt() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.iterator()).thenReturn(filterRecommendationRequests.iterator());
        requestFilterDto.setCreatedAt(localDateTime1);
        recommendationRequestService.getRequest(requestFilterDto);
        verify(recommendationRequestMapper, times(1)).toDto(captor.capture());
        assertEquals(expected.getCreatedAt(), captor.getValue().getCreatedAt());
    }

    @Test
    void testFilterUpdateAt() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.iterator()).thenReturn(filterRecommendationRequests.iterator());
        requestFilterDto.setUpdatedAt(localDateTime2);
        recommendationRequestService.getRequest(requestFilterDto);
        verify(recommendationRequestMapper, times(1)).toDto(captor.capture());
        assertEquals(expected.getUpdatedAt(), captor.getValue().getUpdatedAt());
    }

    @Test
    void testRejectFail() {
        requestForRejected.setStatus(RequestStatus.REJECTED);
        when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(requestForRejected));
        Assert.assertThrows(RejectFailException.class, () -> recommendationRequestService.rejectRequest(7L, rejectionDto));
    }

    @Test
    void testRejectCompleted() {
        requestForRejected.setStatus(RequestStatus.PENDING);
        when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(requestForRejected));
        recommendationRequestService.rejectRequest(7L, rejectionDto);
        verify(recommendationRequestMapper, times(1)).toDto(captor.capture());
        assertEquals(RequestStatus.REJECTED, captor.getValue().getStatus());
    }
}
