package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestValidatorTest {
    private RecommendationRequestDto recommendationRequest;
    @Mock
    private UserService userValidator;
    @Mock
    private SkillService skillValidator;
    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;

    @BeforeEach
    void setUp() {
        SkillRequest skillRequest1 = new SkillRequest();
        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest1.setId(1);
        skillRequest2.setId(2);

        recommendationRequest = RecommendationRequestDto.builder()
                .id(5L)
                .message("message")
                .status(RequestStatus.REJECTED)
                .skills(List.of(skillRequest1, skillRequest2))
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testValidateUsersExist() {
        recommendationRequest.setRequesterId(5L);
        recommendationRequest.setReceiverId(4L);
        recommendationRequestValidator.validateUsersExist(recommendationRequest);
        verify(userValidator).validateUsers(5L, 4L);
    }

    @Test
    public void testSkillExistsValidation() {
        SkillRequest skillRequest1 = new SkillRequest();
        SkillRequest skillRequest2 = new SkillRequest();
        skillRequest1.setId(11);
        skillRequest2.setId(15);
        recommendationRequest = RecommendationRequestDto.builder()
                .skills(List.of(skillRequest1, skillRequest2))
                        .build();
        recommendationRequestValidator.validateSkillsExist(recommendationRequest);
        verify(skillValidator).validateSkills(List.of(skillRequest1.getId(), skillRequest2.getId()));
    }
}
