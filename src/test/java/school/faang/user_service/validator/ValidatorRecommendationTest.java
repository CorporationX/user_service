package school.faang.user_service.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.validator.recommendation.ValidatorRecommendationImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class ValidatorRecommendationTest {
    @InjectMocks
    private ValidatorRecommendationImpl validator;

    @Test
    public void testValidateStatusPending() {
        RequestStatus status = RequestStatus.PENDING;
        String paramName = "status";

        assertDoesNotThrow(() -> validator.validateStatus(status));
    }

    @Test
    public void testValidateStatusNotPending() { // Arrange
        RequestStatus status = RequestStatus.REJECTED;
        String paramName = "status";

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateStatus(status)
        );
    }

    @Test
    public void testValidateRecommendationToRequest() {
        Long id = 1L;
        Long receiverId = 1L;
        String paramName = "Id";

        assertDoesNotThrow(() -> validator.validateRecommendationToRequest(id, receiverId, paramName));
    }

    @Test
    public void testValidateRecommendationNotRequest() {
        Long id = 1L;
        Long receiverId = 2L;
        String paramName = "Id";

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateRecommendationToRequest(id, receiverId, paramName)
        );
    }

    @Test
    public void testValidateTimeOutSixMount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recentDate = now.minusMonths(7);
        String paramName = "Date";

        assertDoesNotThrow(() -> validator.validateTimeOutSixMount(recentDate, paramName));
    }

    @Test
    public void testValidateNotTimeOutSixMount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldDate = now.plusDays(1);
        String paramName = "Date";

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateTimeOutSixMount(oldDate, paramName)
        );
    }

    @Test
    public void testValidateRecommendationIsRequest() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        String paramName = "Id";

        assertDoesNotThrow(() -> validator.validateRecommendationIsRequest(requesterId, receiverId, paramName));
    }

    @Test
    public void testValidateRecommendationIsNotRequest() {
        Long requesterId = 1L;
        Long receiverId = 1L;
        String paramName = "Id";

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> validator.validateRecommendationIsRequest(requesterId, receiverId, paramName)
        );
    }
}
