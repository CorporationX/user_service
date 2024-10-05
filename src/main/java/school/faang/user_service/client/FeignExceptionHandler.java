package school.faang.user_service.client;

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

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class FeignExceptionHandler {
    private static final String MESSAGE_FIELD = "message";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ProblemDetail> handleJsonProcessingException(FeignException exception) {
        log.error(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handleFeignException(FeignException exception)
            throws JsonProcessingException {
        var errorMessage = exception.getMessage();
        errorMessage = getErrorResponseMessage(exception, errorMessage);
        log.error(errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage));
    }

    private String getErrorResponseMessage(FeignException exception, String errorMessage)
            throws JsonProcessingException {
        String responseBody = exception.contentUTF8();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        if (responseJson.has(MESSAGE_FIELD)) {
            errorMessage = responseJson.get(MESSAGE_FIELD).asText();
        }
        return errorMessage;
    }
}
