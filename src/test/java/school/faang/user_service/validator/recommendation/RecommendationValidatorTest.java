package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.CreateRecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.exception.recommendation.AnotherAuthorException;
import school.faang.user_service.exception.recommendation.RecommendationCooldownException;
import school.faang.user_service.exception.recommendation.SelfRecommendationException;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationValidatorTest {

    @InjectMocks
    private RecommendationValidator recommendationValidator;

    @Mock
    private UserContext userContext;

    @Mock
    private RecommendationRepository recommendationRepository;

    private final long userId = 1L;
    private static final int MIN_DELAY_MONTHS = 6;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recommendationValidator, "minDelayMonths", MIN_DELAY_MONTHS);
        when(userContext.getUserId()).thenReturn(userId);
    }

    @Test
    void validateCreateThrowsForSelfRecommendation() {
        String content = "I am great!";
        CreateRecommendationDto createDto = new CreateRecommendationDto(userId, content);

        assertThrows(SelfRecommendationException.class, () -> recommendationValidator.validateCreate(createDto));
    }

    @Test
    void validateCreateThrowsForRecommendationCooldownViolation() {
        Recommendation prevRecommendation = new Recommendation();
        prevRecommendation.setCreatedAt(LocalDateTime.now().minusDays(1));
        long receiverId = 2L;
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(userId, receiverId))
                .thenReturn(Optional.of(prevRecommendation));
        String content = "Cool down!";
        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, content);

        assertThrows(RecommendationCooldownException.class, () -> recommendationValidator.validateCreate(createDto));
    }

    @Test
    void validateCreateDoesNotThrowIfNoPriorRecommendationFromSameAuthorExists() {
        long receiverId = 2L;
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(userId, receiverId))
                .thenReturn(Optional.empty());

        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, "Fresh one!");

        assertDoesNotThrow(() -> recommendationValidator.validateCreate(createDto));
    }

    @Test
    void validateCreateDoesNotThrowWhenCooldownExactlyMet() {
        long receiverId = 2L;
        Recommendation prevRecommendation = new Recommendation();
        prevRecommendation.setCreatedAt(LocalDateTime.now().minusMonths(MIN_DELAY_MONTHS));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(userId, receiverId))
                .thenReturn(Optional.of(prevRecommendation));

        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, "Right on time");

        assertDoesNotThrow(() -> recommendationValidator.validateCreate(createDto));
    }

    @Test
    void validateCreateDoesNotThrowForValidData() {
        long receiverId = 2L;
        int monthsSinceLastRecommendation = MIN_DELAY_MONTHS + 1;
        Recommendation prevRecommendation = new Recommendation();
        prevRecommendation.setCreatedAt(LocalDateTime.now().minusMonths(monthsSinceLastRecommendation));
        when(recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(userId, receiverId))
                .thenReturn(Optional.of(prevRecommendation));
        String content = "All valid!";
        CreateRecommendationDto createDto = new CreateRecommendationDto(receiverId, content);

        assertDoesNotThrow(() -> recommendationValidator.validateCreate(createDto));
    }

    @Test
    void validateUpdateThrowsForDifferentAuthor() {
        long otherUserId = 2L;
        Recommendation recommendation = getRecommendation(otherUserId, userId);

        assertThrows(AnotherAuthorException.class, () -> recommendationValidator.validateUpdate(recommendation));
    }

    @Test
    void validateUpdateDoesNotThrowForSameAuthor() {
        Recommendation recommendation = getRecommendation(userId, userId);

        assertDoesNotThrow(() -> recommendationValidator.validateUpdate(recommendation));
    }

    @Test
    void validateDeleteThrowsForDifferentAuthor() {
        long otherUserId = 2L;
        Recommendation recommendation = getRecommendation(otherUserId, userId);

        assertThrows(AnotherAuthorException.class, () -> recommendationValidator.validateDelete(recommendation));
    }

    @Test
    void validateDeleteDoesNotThrowForSameAuthor() {
        Recommendation recommendation = getRecommendation(userId, userId);

        assertDoesNotThrow(() -> recommendationValidator.validateDelete(recommendation));
    }

    private Recommendation getRecommendation(long authorId, long receiverId) {
        User author = new User();
        author.setId(authorId);
        User receiver = new User();
        receiver.setId(receiverId);
        return Recommendation.builder().author(author).receiver(receiver).build();
    }
}