package school.faang.user_service.controller.premium;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import school.faang.user_service.exception.premium.PremiumCheckFailureException;
import school.faang.user_service.exception.premium.PremiumNotFoundException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class PremiumControllerAdvice {
    private static final String DEFAULT_MESSAGE = "Unknown error occurred";
    private static final String MESSAGE_FIELD = "message";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(PremiumCheckFailureException.class)
    public ResponseEntity<ProblemDetail> premiumCheckFailureExceptionHandler(PremiumCheckFailureException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(PremiumNotFoundException.class)
    public ResponseEntity<ProblemDetail> premiumNotFoundExceptionHandler(PremiumNotFoundException exception) {
        log.info(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> feignExceptionHandler(FeignException exception) {
        var errorMessage = getErrorResponseMessage(exception);
        log.info(errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage));
    }

    private String getErrorResponseMessage(FeignException exception) {
        String errorMessage = DEFAULT_MESSAGE;
        try {
            String responseBody = exception.contentUTF8();
            JsonNode responseJson = objectMapper.readTree(responseBody);
            if (responseJson.has(MESSAGE_FIELD)) {
                errorMessage = responseJson.get(MESSAGE_FIELD).asText();
            }
        } catch (JsonProcessingException exc) {
            log.info("Json processing exception: {}", exc.getMessage());
        }
        return errorMessage;
    }
}
