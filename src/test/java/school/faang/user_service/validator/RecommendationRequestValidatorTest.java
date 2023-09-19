package school.faang.user_service.validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestValidatorTest {
    @InjectMocks
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private UserRepository userRepository;

    private RecommendationRequest request;

    @BeforeEach
    void setUp() {
        request = RecommendationRequest.builder()
                .createdAt(LocalDateTime.now().minusMonths(5))
                .build();
    }

    @Test
    void testValidationExistByIdThrowDataValidationException() {
        assertThrows(DataValidationException.class, () -> recommendationRequestValidator.validationExistById(1L));
    }
    @Test
    void testValidationExistByIdDoesNotThrowDataValidationException() {
        Mockito.when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> recommendationRequestValidator.validationExistById(1L));
    }

    @Test
    void testValidationRequestDateNegative() {
        Optional<RecommendationRequest> recommendationRequest = Optional.of(request);
        assertThrows(DataValidationException.class, () -> recommendationRequestValidator.validationRequestDate(recommendationRequest));
    }

    @Test
    void testValidationRequestDatePositive() {
        request.setCreatedAt(LocalDateTime.now().minusMonths(7));
        Optional<RecommendationRequest> recommendationRequest = Optional.of(request);
        assertDoesNotThrow(() -> recommendationRequestValidator.validationRequestDate(recommendationRequest));
    }
}