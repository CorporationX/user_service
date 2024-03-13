package school.faang.user_service.service.recommendation;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidationTest {
    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private RejectionDto rejectionDto;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @BeforeEach
    void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .requesterId(2L)
                .receiverId(3L)
                .message("message")
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(8L)
                .requester(new User())
                .receiver(new User())
                .message("message 2")
                .build();

        rejectionDto = RejectionDto.builder()
                .reason("message")
                .build();
    }

    @Test
    public void testRecommendationRequestValidatorUserNotFound() {
        long invalidId = 1555;
        Assert.assertThrows(EntityNotFoundException.class, () -> recommendationRequestValidator.validateUser(invalidId));
    }

    @Test
    public void testRecommendationRequestValidatorSkills() {
        List<Long> invalidSkillsIds = List.of(111L, 112L, 113L);
        Assert.assertThrows(EntityNotFoundException.class, () -> recommendationRequestValidator.validateSkills(invalidSkillsIds));
    }

}
