package school.faang.user_service.service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.rejection.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.filter.recommendation.RecommendationRequestMessageFilter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    private RecommendationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("Hello")
                .status(RequestStatus.ACCEPTED)
                .skillIds(List.of(1L))
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
    }

    @Test
    void testThrowValidationExceptionByUserId() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testThrowValidationExceptionBySkillId() {
        Mockito.lenient().when(skillRepository.existsById(1L)).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testNotThrowValidationException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testNotThrowValidationExceptionByRequestTime() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(5));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testThrowValidationExceptionByRequestTime() {
        requestDto.setCreatedAt(LocalDateTime.now().minusMonths(10));
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testThrowValidationExceptionByRequestId() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> recommendationRequestService.getRequest(1L));
    }

    @Test
    void testNotThrowValidationExceptionByRequestId() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        assertDoesNotThrow(() -> recommendationRequestService.getRequest(1L));
    }

    @Test
    void testThrowValidationExceptionByRejectedRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        RejectionDto rejectionDto = RejectionDto.builder().reason("Reason").build();
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        assertThrows(DataValidationException.class, () -> recommendationRequestService.rejectRequest(1L, rejectionDto));
    }

    @Test
    void testNotThrowValidationExceptionByRejectedRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.PENDING);
        RejectionDto rejectionDto = RejectionDto.builder().reason("Reason").build();
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        assertDoesNotThrow(() -> recommendationRequestService.rejectRequest(1L, rejectionDto));
    }

    @Test
    void testRecommendationRequestFilter() {
        List<RecommendationRequestFilter> requestFilters = List.of(new RecommendationRequestMessageFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest2.setMessage("Goodbye");
        recommendationRequest3.setMessage("");

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder().messagePattern("Hell").build();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, userRepository, skillRepository, requestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getRecommendationRequests(recommendationRequestFilterDto);
        assertEquals(1, eventsByFilter.size());
    }
}