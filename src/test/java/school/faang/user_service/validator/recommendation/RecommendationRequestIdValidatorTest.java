package school.faang.user_service.validator.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

public class RecommendationRequestIdValidatorTest {
    @InjectMocks
    private RecommendationRequestIdValidator recommendationRequestIdValidator;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    private Long id;

    @BeforeEach
    public void setUp() {
        id = 1L;

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getting NoSuchElementException when there is no recommendation request with given id")
    public void testValidateId() {
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> recommendationRequestIdValidator.validateId(id));
    }
}
