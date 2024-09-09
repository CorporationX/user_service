package school.faang.user_service.controller.premium;

import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class PremiumControllerAdviceTest {
    private static final String TEST_MESSAGE = "test message";

    @Mock
    private PremiumCheckFailureException premiumCheckFailureException;

    @Mock
    private PremiumNotFoundException premiumNotFoundException;

    @Mock
    private FeignException feignException;

    @InjectMocks
    private PremiumControllerAdvice premiumControllerAdvice;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("Test premium check failure exception handler")
    void testPremiumCheckFailureExceptionSuccessful() {
        when(premiumCheckFailureException.getMessage()).thenReturn(TEST_MESSAGE);
        var response = premiumControllerAdvice.premiumCheckFailureExceptionHandler(premiumCheckFailureException);
        assertResponse(response, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Test premium not found exception handler")
    void testPremiumNotFoundExceptionHandlerSuccessful() {
        when(premiumNotFoundException.getMessage()).thenReturn(TEST_MESSAGE);
        var response = premiumControllerAdvice.premiumNotFoundExceptionHandler(premiumNotFoundException);
        assertResponse(response, HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Test http client error exception handler")
    void testHttpClientErrorExceptionHandlerSuccessful() {
        when(feignException.getMessage()).thenReturn(TEST_MESSAGE);
        var response = premiumControllerAdvice.httpClientErrorExceptionHandler(feignException);
        assertResponse(response, HttpStatus.BAD_REQUEST);
    }

    private void assertResponse(ResponseEntity<ProblemDetail> response, HttpStatus httpStatus) {
        assertThat(response)
                .isNotNull()
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(httpStatus);
        assertThat(response.getBody())
                .isNotNull()
                .extracting(ProblemDetail::getStatus)
                .isEqualTo(httpStatus.value());
        assertThat(response.getBody())
                .extracting(ProblemDetail::getDetail)
                .isEqualTo(TEST_MESSAGE);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}