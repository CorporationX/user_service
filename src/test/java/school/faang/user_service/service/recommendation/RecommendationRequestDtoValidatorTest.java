package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class RecommendationRequestDtoValidatorTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private RecommendationRequestDtoValidator recommendationRequestDtoValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEmplyMessage() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("");

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateRecommendationRequestDto(recommendationRequestDto));
    }

    @Test
    public void testCreateNotExistIds() {
        Long id = 1L;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(id);
        recommendationRequestDto.setRequesterId(id);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationRequestDto.getRequesterId(),
                recommendationRequestDto.getReceiverId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                recommendationRequestDtoValidator.validateRecommendationRequestDto(recommendationRequestDto));
    }

    @Test
    public void testTimeRequestDiference() {
        Long id  = 1L;
        int monthsDifference = 5;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(id);
        recommendationRequestDto.setRequesterId(id);

        Recommendation recommendation = new Recommendation();
        recommendation.setId(id);

        LocalDateTime currentRequestTime = LocalDateTime.now();
        LocalDateTime latestRequestTime = currentRequestTime.plusMonths(monthsDifference);
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequestDto.setCreatedAt(currentRequestTime);
        recommendationRequest.setCreatedAt(latestRequestTime);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateRecommendationRequestDto(recommendationRequestDto));
    }

    @Test
    public void testAllSkillExists() {
        Long id = 1L;
        int monthsDifference = 7;
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("Test message");
        recommendationRequestDto.setReceiverId(id);
        recommendationRequestDto.setRequesterId(id);
        Skill skill = new Skill();
        skill.setTitle("Test title");
        recommendationRequestDto.setSkills(List.of(new SkillRequest(id, new RecommendationRequest(), skill)));

        Recommendation recommendation = new Recommendation();
        recommendation.setId(id);

        LocalDateTime currentRequestTime = LocalDateTime.now();
        LocalDateTime latestRequestTime = currentRequestTime.plusMonths(monthsDifference);
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequestDto.setCreatedAt(currentRequestTime);
        recommendationRequest.setCreatedAt(latestRequestTime);

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendation));

        when(recommendationRequestRepository.findLatestPendingRequest(anyLong(), anyLong()))
                .thenReturn(Optional.of(recommendationRequest));

        when(skillRepository.existsByTitle(anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                recommendationRequestDtoValidator.validateRecommendationRequestDto(recommendationRequestDto));
    }
}
