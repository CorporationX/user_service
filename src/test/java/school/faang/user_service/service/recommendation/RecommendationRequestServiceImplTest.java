package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
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

    private RecommendationRequestDto prepareData() {
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
}