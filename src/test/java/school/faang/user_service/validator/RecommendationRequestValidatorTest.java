package school.faang.user_service.validator;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @BeforeEach
    void setUp() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(1);

        recommendationRequest = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(List.of(skillRequest))
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testRequesterNotExistValidation() {
        recommendationRequest.setRequesterId(8L);
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
    public void testSkillNotExistValidation() {
        Assert.assertThrows(
                EntityNotFoundException.class,
                () -> recommendationRequestValidator.validateSkillsExist(recommendationRequest)
        );
    }

    @Test
    public void testSkillExistsValidation() {
        Mockito.when(skillRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validateSkillsExist(recommendationRequest));
    }
}
