package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.impl.RecommendationServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceImplTest {

    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long RECOMMENDATION_ID = 1L;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private RecommendationDto recommendationDto;
    private Recommendation recommendation;
    private User author;
    private User receiver;
    private Skill skill;

    @BeforeEach
    void setUp() {
        recommendationDto = new RecommendationDto(RECOMMENDATION_ID, RECEIVER_ID, AUTHOR_ID,
                "Recommendation content", List.of(new SkillOfferDto(SKILL_ID, RECOMMENDATION_ID, SKILL_ID)), LocalDateTime.now());

        author = User.builder().id(AUTHOR_ID).build();
        receiver = User.builder().id(RECEIVER_ID).build();
        skill = Skill.builder().id(SKILL_ID).build();
        recommendation = Recommendation.builder().build();
    }

    @Test
    @DisplayName("Обновление рекомендации: рекомендация не найдена")
    void updateRecommendation_ShouldThrowException_WhenRecommendationNotFound() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationService.updateRecommendation(RECOMMENDATION_ID, recommendationDto));

        assertEquals("Recommendation with id " + RECOMMENDATION_ID + " not found", exception.getMessage());
    }

    @Test
    @DisplayName("Удаление рекомендации с корректным ID")
    void deleteRecommendation_ShouldDeleteSuccessfully() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));

        recommendationService.deleteRecommendation(RECOMMENDATION_ID);

        verify(recommendationRepository).delete(recommendation);
    }

    @Test
    @DisplayName("Получение всех рекомендаций для получателя")
    void getAllUserRecommendations_ShouldReturnRecommendations_WhenValid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID, pageable);

        assertFalse(result.isEmpty());
        verify(recommendationRepository).findAllByReceiverId(RECEIVER_ID, pageable);
    }

    @Test
    @DisplayName("Получение всех рекомендаций от автора")
    void getAllGivenRecommendations_ShouldReturnRecommendations_WhenValid() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Recommendation> recommendationPage = new PageImpl<>(List.of(recommendation));
        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, pageable)).thenReturn(recommendationPage);
        when(recommendationMapper.toRecommendationDto(recommendation)).thenReturn(recommendationDto);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(AUTHOR_ID, pageable);

        assertFalse(result.isEmpty());
        verify(recommendationRepository).findAllByAuthorId(AUTHOR_ID, pageable);
    }

    @Test
    @DisplayName("Удаление рекомендации с корректным ID")
    void deleteRecommendation_ShouldDeleteRecommendation_WhenValid() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));

        recommendationService.deleteRecommendation(RECOMMENDATION_ID);

        verify(recommendationRepository).delete(recommendation);
    }

    @Test
    @DisplayName("Удаление рекомендации: рекомендация не найдена")
    void deleteRecommendation_ShouldThrowException_WhenRecommendationNotFound() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                recommendationService.deleteRecommendation(RECOMMENDATION_ID));

        assertEquals("Recommendation with id " + RECOMMENDATION_ID + " not found", exception.getMessage());
    }
}