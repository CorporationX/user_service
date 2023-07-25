package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RecommendationRequestServiceTest {
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @BeforeEach
    void setUp() {
        recommendationRequest = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(null)
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testRequesterNotExistValidation() {
        recommendationRequest.setRequesterId(8888888L);
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateUsersExist(recommendationRequest)
        );
    }

    @Test
    public void testReceiverNotExistValidation() {
        recommendationRequest.setReceiverId(8888888L);
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateUsersExist(recommendationRequest)
        );
    }

    @Test
    void testUserExistsValidation() {
        recommendationRequest.setRequesterId(1L);
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateUsersExist(recommendationRequest));
    }

    @Test
    public void testSkillNotExistValidation() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateSkillsExist(recommendationRequest)
        );
    }

    @Test
    void testSkillExistsValidation() {
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateSkillsExist(recommendationRequest));
    }

    @Test
    public void testRequestPeriodValidation() {
        recommendationRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));
        Assert.assertThrows(
                DataValidationException.class,
                () -> recommendationRequestValidator.validateRequestPeriod(recommendationRequest)
        );
    }
  
    @Test
    public void testRequestNotFound() {
        long invalidId = 1236;
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestService.getRequest(invalidId)
          } 
  
    @Test
    public void testRequestFound() {
        long validId = 55;
        recommendationRequestService.getRequest(validId);
        Mockito.verify(recommendationRequestRepository, Mockito.times(1)).findById(validId);
    }
}
