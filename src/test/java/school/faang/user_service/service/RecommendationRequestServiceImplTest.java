package school.faang.user_service.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.RecommendationRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    private RecommendationRequest recommendationRequest;
    private RecommendationRequestDto recommendationRequestDto;
    private RejectionDto rejectionDto;

    @BeforeEach
    public void setUp() {
        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        rejectionDto = new RejectionDto();
        rejectionDto.setReason("Some rejection reason");

        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setId(recommendationRequest.getId());
        recommendationRequestDto.setStatus(RequestStatus.REJECTED);

        recommendationRequestDto.setRequesterId(1L);
        recommendationRequestDto.setReceiverId(2L);
        List<Long> skillIds = List.of(3L);
        recommendationRequestDto.setSkillId(skillIds);
    }

    @Test
    public void testRejectRequest_Success() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequestDto);

        recommendationRequestService.rejectRequest(1L, rejectionDto);

        verify(recommendationRequestRepository).findById(1L);
        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationRequestMapper).toDto(recommendationRequest);
    }

    @Test
    public void testCreate_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.empty());
        when(userRepository.countOwnedSkills(1L, List.of(3L))).thenReturn(1);
        when(recommendationRequestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class)))
                .thenReturn(recommendationRequest);
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.create(recommendationRequestDto);

        assertNotNull(result);
        assertEquals(recommendationRequestDto.getId(), result.getId());
        verify(userRepository).existsById(1L);
        verify(userRepository).existsById(2L);
        verify(recommendationRequestRepository).findLatestPendingRequest(1L, 2L);
        verify(userRepository).countOwnedSkills(1L, List.of(3L));
        verify(recommendationRequestMapper).toEntity(recommendationRequestDto);
        verify(recommendationRequestRepository).save(recommendationRequest);
        verify(recommendationRequestMapper).toDto(recommendationRequest);
        }

    @Test
    public void testCreate_RequesterNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });
        assertEquals("Requester not found", thrown.getMessage());
    }

    @Test
    public void testCreate_ReceiverNotFound() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        NullPointerException thrown = assertThrows(NullPointerException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });
        assertEquals("Receiver not found", thrown.getMessage());
    }

    @Test
    public void testCreate_TooFrequentRequest() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);

        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(5));
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(existingRequest));

        RecommendationRequestException thrown = assertThrows(
                RecommendationRequestException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });
        assertEquals("Recommendation request can be sent only once in 6 months", thrown.getMessage());
    }
}