package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RecommendationRequestDtoValidatorTest {
    @InjectMocks
    private RecommendationRequestDtoValidator recommendationRequestDtoValidator;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    private Long requesterId;
    private Long receiverId;
    private String emptyMessage;
    private int monthsDifference;
    private Skill skill;
    private SkillRequest skillRequest;
    private List<SkillRequest> skills;
    private RecommendationRequest recommendationRequest = new RecommendationRequest();

    @BeforeEach
    public void setUp() {
        requesterId = 1L;
        receiverId = 2L;
        emptyMessage = "";

        recommendationRequest = new RecommendationRequest();
        skillRequest = new SkillRequest();

        skill = new Skill();
        skills = new ArrayList<>();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getting IllegalArgumentException when emptyMessage was given")
    public void testValidateMessage() {
        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateMessage(emptyMessage));
    }

    @Test
    @DisplayName("Test getting EntityNotFoundException when there is no requester and receiver id in database")
    public void testValidateRequesterAndReceiverIds() {
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(requesterId, receiverId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestDtoValidator.validateRequesterAndReceiverIds(requesterId, receiverId));
    }

    @Test
    @DisplayName("Test getting IllegalArgumentException when time interval between last and new recommendation requests less than 6 months")
    public void testValidateRequestTimeDifference() {
        monthsDifference = 5;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime latestRequestTime = createdAt.plusMonths(monthsDifference);
        recommendationRequest.setCreatedAt(latestRequestTime);

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateRequestTimeDifference(createdAt, requesterId, receiverId));
    }

    @Test
    @DisplayName("Test getting IllegalArgumentException when there is no any of requested skills in database")
    public void testValidateRequestedSkills() {
        skill.setTitle("Test title");
        skillRequest.setSkill(skill);
        skills.add(skillRequest);

        when(skillRepository.existsByTitle(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateRequestedSkills(skills));
    }
}
