package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationDtoValidatorTest {

    @InjectMocks
    private RecommendationDtoValidator recommendationDtoValidator;
    @Mock
    private RecommendationRepository recommendationRepository;

    private final long USER_ID = 1;

    @Test
    public void testCheckIfRecommendationContentIsBlank() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent(" ");

        assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.checkIfRecommendationContentIsBlank(recommendationDto));
    }

    @Test
    public void testCheckIfRecommendationCreatedTimeIsShort() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setContent("content");
        recommendationDto.setAuthorId(USER_ID);
        recommendationDto.setReceiverId(USER_ID);
        recommendationDto.setCreatedAt(LocalDateTime.now());

        Recommendation recommendation = new Recommendation();
        recommendation.setCreatedAt(LocalDateTime.now());
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(), recommendationDto.getReceiverId()))
                .thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class,
                () -> recommendationDtoValidator.checkIfRecommendationCreatedTimeIsShort(recommendationDto));
    }
}