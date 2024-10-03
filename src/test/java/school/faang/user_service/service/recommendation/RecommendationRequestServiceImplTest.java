package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class RecommendationRequestServiceImplTest {

    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private UserRepository userRepository;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private SkillRequest skillRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setReceiverId(1L);
        recommendationRequestDto.setRequesterId(2L);

        recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        skillRequest = new SkillRequest();
        skillRequest.setId(1L);
    }

    @Test
    void getRequest_ShouldReturnRequest_WhenFound() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RecommendationRequestDto result = recommendationRequestService.getRequest(1L);

        assertEquals(recommendationRequestDto, result);
    }

    @Test
    void getRequest_ShouldThrowException_WhenRequestNotFound() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestService.getRequest(1L));

        assertEquals("Not found RequestRecommendation for id: 1", exception.getMessage());
    }
}