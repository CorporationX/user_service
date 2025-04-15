package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.TestRecommendationRequestAcceptedFilterStutus;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestFilter filter = new TestRecommendationRequestAcceptedFilterStutus();
    @Mock
    private UserService userService;
    @Mock
    private SkillRequestService skillRequestService;
    private RecommendationRequestService recommendationRequestService;
    private RecommendationRequest firstRecommendationRequest;
    private RecommendationRequest secondRecommendationRequest;

    @BeforeEach
    public void setUp() {
        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,
                recommendationRequestMapper, List.of(filter), userService, skillRequestService);
    }

    @Test
    public void tesPositiveGetFilteredRecommendationRequestsAcceptedIsFiltered() {
        getfirstAndSecondRecommendationRequest();
        when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(firstRecommendationRequest, secondRecommendationRequest));

        List<RecommendationRequestDto> requestDtos = recommendationRequestService
                .getFilteredRecommendationRequests(new RequestFilterDto(null));

        assertFalse(requestDtos.isEmpty(), "Expected the filtered list to be empty");
        assertEquals(1, requestDtos.size());
    }

    @Test
    public void testPositiveRequesterAndReceiverAndCreatedDateAfter() {
        RecommendationRequestDto dto = RecommendationRequestDto.builder()
                .requesterId(1L)
                .receiverId(2L)
                .message("Please recommend me!")
                .skillsId(Arrays.asList(1L, 2L))
                .build();

        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        when(skillRequestService.findByIds(dto.getSkillsId())).thenReturn(new ArrayList<>());
        when(userService.findById(dto.getRequesterId())).thenReturn(Optional.of(requester));
        when(userService.findById(dto.getReceiverId())).thenReturn(Optional.of(receiver));

        recommendationRequestService.create(dto);

        verify(recommendationRequestRepository, times(1)).save(argThat(savedRequest -> {
            assertEquals(requester, savedRequest.getRequester());
            assertEquals(receiver, savedRequest.getReceiver());
            assertEquals(dto.getMessage(), savedRequest.getMessage());
            return true;
        }));
    }

    @Test
    public void testPositiveRejectRequestStatusAccepted() {
        final long requestId = 1L;
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .build();

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("запрос отклонен");

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        final RecommendationRequestDto dto = recommendationRequestService
                .rejectRequest(requestId, rejectionDto);

        verify(recommendationRequestRepository, times(1)).save(request);
        assertEquals(RequestStatus.REJECTED, request.getStatus());
        assertEquals("запрос отклонен", request.getRejectionReason());
        assertEquals(dto.getId(), request.getId());
    }


    @Test
    public void testPositiveGetRecommendationRequestById() {
        long id = 1L;
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(id);

        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(request1));

        RecommendationRequestDto dto1 = recommendationRequestService.getRecommendationRequestById(id);
        assertEquals(dto.getId(), dto1.getId());
        assertNotNull(dto1);

    }

    @Test
    public void testNegativeGetRecommendationRequestByIdNotFound() {
        long id = 1L;
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(id);
        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(id);

        when(recommendationRequestRepository.findById(id)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> recommendationRequestService.getRecommendationRequestById(id));

    }

    @Test
    public void testNegativeRejectRequestStatusNotPending() {
        final long requestId = 1L;
        RecommendationRequest request = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.ACCEPTED)
                .build();
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("запрос отклонен");

        when(recommendationRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(IllegalStateException.class, () -> recommendationRequestService
                .rejectRequest(requestId, rejectionDto));
    }

    @Test
    public void testNegativeGetFilteredRecommendationRequestsAcceptedIsNotFiltered() {
        List<RecommendationRequestDto> requestDtos = recommendationRequestService
                .getFilteredRecommendationRequests(new RequestFilterDto(null));

        assertTrue(requestDtos.isEmpty(), "Expected the filtered list to be empty");
        assertEquals(0, requestDtos.size());
    }

    @Test
    public void testNegativeRequesterAndReceiverAndCreatedDateAfterIsNot() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("Please recommend me!");

        User requester = new User();
        User receiver = new User();

        when(userService.findById(1L)).thenReturn(Optional.of(requester));
        when(userService.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestRepository.findByRequesterAndReceiverAndCreatedAtAfter(any(User.class),
                any(User.class), any(LocalDateTime.class))).thenReturn(Optional.of(new RecommendationRequest()));

        assertThrows(IllegalStateException.class, () -> recommendationRequestService.create(dto));
    }

    @Test
    public void testNegativeCreatedDtoNull() {
        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(null));
    }

    @Test
    public void testNegativeCreatedDtosetRequesterIdNull() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(3L);
        assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(dto));
    }

    private void getfirstAndSecondRecommendationRequest() {
        firstRecommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .status(RequestStatus.PENDING)
                .build();
        secondRecommendationRequest = RecommendationRequest.builder()
                .id(2L)
                .status(RequestStatus.ACCEPTED)
                .build();
    }
}

