package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private List<RecommendationRequestFilter> recommendationRequestFilters;
    @Mock
    private RecommendationRequestRepository repository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;

    private RecommendationRequest request;
    private RecommendationRequestDto dto;

    @BeforeEach
    void setUp() {
        request = new RecommendationRequest();
        dto = prepareData();
    }

    // Test for create method
    @Test
    void create_ShouldThrowException_WhenReceiverDoesNotExist() {
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(dto)
        );

        assertEquals("Receiver with ID 1 does not exist.", exception.getMessage());
    }

    @Test
    void create_ShouldThrowException_WhenRequesterDoesNotExist() {
        when(userRepository.existsById(dto.getReceiverId())).thenReturn(true);
        when(userRepository.existsById(dto.getRequesterId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestService.create(dto)
        );

        assertEquals("Requester with ID 2 does not exist.", exception.getMessage());
    }

    @Test
    void create_ShouldValidateAndSaveRequest_WhenAllValidationsPass() {
        Long receiverId = dto.getReceiverId();
        Long requesterId = dto.getRequesterId();

        when(userRepository.existsById(receiverId)).thenReturn(true);
        when(userRepository.existsById(requesterId)).thenReturn(true);
        when(repository.findLatestPendingRequest(requesterId, receiverId)).thenReturn(Optional.empty());

        when(recommendationRequestMapper.toEntity(dto)).thenReturn(request);
        when(repository.save(request)).thenReturn(request);
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.create(dto);

        verify(recommendationRequestValidator).validateRequesterAndReceiver(requesterId, receiverId);
        verify(recommendationRequestValidator).validateRequestAndCheckTimeLimit(null);
        verify(repository).save(request);
        assertEquals(dto, result);
    }

    private RecommendationRequestDto prepareData(){
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setReceiverId(1L);
        dto.setRequesterId(2L);

        return dto;
    }

    // Test for getRequest method
    @Test
    void getRequest_ShouldThrowException_WhenRequestDoesNotExist() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestService.getRequest(1L)
        );

        assertEquals("Not found RequestRecommendation for id: 1", exception.getMessage());
    }

    @Test
    void getRequest_ShouldReturnRequest_WhenExists() {
        RecommendationRequestDto dto = new RecommendationRequestDto();

        when(repository.findById(1L)).thenReturn(Optional.of(request));
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(1L);

        assertEquals(dto, result);
    }

    // Test for rejectRequest method
    @Test
    void rejectRequest_ShouldThrowException_WhenRequestIsNotPending() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setStatus(RequestStatus.ACCEPTED);

        when(recommendationRequestService.getRequest(1L)).thenReturn(dto);

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Reason");

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationRequestService.rejectRequest(1L, rejectionDto)
        );

        assertEquals("It is impossible to refuse a request that is not in a pending state", exception.getMessage());
    }

    @Test
    void rejectRequest_ShouldUpdateStatusToRejected_WhenRequestIsPending() throws DataValidationException {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setStatus(RequestStatus.PENDING);

        when(recommendationRequestService.getRequest(1L)).thenReturn(dto);

        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("Reason");

        when(recommendationRequestMapper.toEntity(dto)).thenReturn(request);
        when(recommendationRequestMapper.toDto(request)).thenReturn(dto);

        RecommendationRequestDto result = recommendationRequestService.rejectRequest(1L, rejectionDto);

        verify(request).setStatus(RequestStatus.REJECTED);
        verify(request).setRejectionReason("Reason");
        assertEquals(dto, result);
    }
}