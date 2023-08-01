package school.faang.user_service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    SkillOfferRepository skillOffersRepository;
    @InjectMocks
    RecommendationValidator recommendationValidator;

    Recommendation recommendation;
    RecommendationDto recommendationDto;
    SkillOfferDto skillOfferDto;
    @BeforeEach
    void setUp() {
        recommendation = Recommendation
                .builder()
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
        skillOfferDto = new SkillOfferDto(1L, 1L, 1L);
        recommendationDto = RecommendationDto
                .builder()
                .authorId(1L)
                .receiverId(1L)
                .content("content")
                .skillOffers(List.of(skillOfferDto))
                .build();
    }

    @Test
    void validateDataThrowException() {
        Mockito.when(recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                        recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateData(recommendationDto));
    }

    @Test
    void validateDataDoesNotThrowException() {
        recommendation = Recommendation
                .builder()
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();

        Mockito.when(recommendationRepository
                        .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(recommendationDto.getAuthorId(),
                                recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(recommendation));
        Mockito.when(skillOffersRepository.existsById(1L))
                .thenReturn(true);

        assertDoesNotThrow(() -> recommendationValidator.validateData(recommendationDto));
    }

    @Test
    void validateRecommendationThrowException() {
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendation(null));

        recommendationDto.setContent(null);
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendation(recommendationDto));

        recommendationDto.setContent("");
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendation(recommendationDto));
    }

    @Test
    void validateRecommendationDoesNotThrowException() {
        assertDoesNotThrow(() -> recommendationValidator.validateRecommendation(recommendationDto));
    }

    @Test
    void validateRecommendationDtoDoesNotThrowException() {
        assertDoesNotThrow(() -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }

    @Test
    void validateRecommendationDtoThrowException() {
        recommendationDto.setAuthorId(null);
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationDto(recommendationDto));

        recommendationDto.setReceiverId(null);
        assertThrows(DataValidationException.class,
                () -> recommendationValidator.validateRecommendationDto(recommendationDto));
    }
}