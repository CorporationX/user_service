package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private RecommendationService recommendationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create_ShouldThrowException_IfRecentRecommendationExists() {
        // Arrange
        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);

        Recommendation recent = new Recommendation();
        recent.setCreatedAt(LocalDateTime.now().minusMonths(1));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.of(recent));

        // Act + Assert
        assertThatThrownBy(() -> recommendationService.create(dto))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("Recent recommendation already exists");

    }

    @Test
    void create_shouldThrowException_IfRecommendationDoesNotExist() {
        // Arrange
        RecommendationDto dto = new RecommendationDto();
        dto.setAuthorId(1L);
        dto.setReceiverId(2L);
        dto.setContent("test");

        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(1L, 2L))
                .thenReturn(Optional.empty());
        when(recommendationRepository.create(1L, 2L, "test"))
                .thenReturn(42L);

        // Act
        RecommendationDto result = recommendationService.create(dto);

        // Assert
        assertThat(result.getId()).isEqualTo(42L);
        verify(recommendationRepository.create(1L, 2L, "test"));
    }
}
