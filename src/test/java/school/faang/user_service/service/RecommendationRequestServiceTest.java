package school.faang.user_service.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import school.faang.user_service.Service.RecommendationRequestService;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@SpringBootTest
public class RecommendationRequestServiceTest {
    @Spy
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private UserRepository userRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequest existingRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingRequest = new RecommendationRequest();
        existingRequest.setStatus(RequestStatus.PENDING);
    }

    @Test
    public void testRejectRequestSuccess() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Not relevant");

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(existingRequest));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(existingRequest);

        RecommendationRequestDto expectedDto = new RecommendationRequestDto();
        when(recommendationRequestMapper.toDto(existingRequest)).thenReturn(expectedDto);

       recommendationRequestService.rejectRequest(1L, rejectionDto);
    }

    @Test
    void testGetRequest_Success() {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setMessage("Test Message");
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setId(1L);
        requestDto.setMessage("Test Message");
        requestDto.setStatus(RequestStatus.PENDING);
        requestDto.setCreatedAt(request.getCreatedAt());
        requestDto.setUpdatedAt(request.getUpdatedAt());

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toDto(request)).thenReturn(requestDto);

        recommendationRequestService.getRequest(1L);
    }

    @Test
    public void testGetRequestsWithFilter() {
        User requester = new User();
        requester.setId(1L);
        User receiver = new User();
        receiver.setId(2L);

        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        request.setMessage("Request 1");
        request.setStatus(RequestStatus.PENDING);
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());

        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(1L);
        filter.setStatus(RequestStatus.PENDING);

        when(recommendationRequestRepository.findAll()).thenReturn(List.of(request));

       recommendationRequestService.getRequests(filter);
    }

    @Test
    void testCreate_RequesterNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        when(userRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        Assertions.assertEquals("Requester not found", thrown.getMessage());
    }

    @Test
    void testCreate_ReceiverNotFound() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        Assertions.assertEquals("Receiver not found", thrown.getMessage());
    }

    @Test
    void testCreate_SkillsNotOwned() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setSkillId(Collections.singletonList(100L));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.countOwnedSkills(1L, Collections.singletonList(100L))).thenReturn(0);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        Assertions.assertEquals("One or more skills do not exist for the requester", thrown.getMessage());
    }

    @Test
    void testCreate_ExistingPendingRequest() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);

        RecommendationRequest existingRequest = new RecommendationRequest();
        existingRequest.setCreatedAt(LocalDateTime.now().minusMonths(3));

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        when(recommendationRequestRepository.findLatestPendingRequest(1L, 2L))
                .thenReturn(Optional.of(existingRequest));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(requestDto);
        });
        Assertions.assertEquals("Recommendation request can be sent only once in 6 months", thrown.getMessage());
    }
}



