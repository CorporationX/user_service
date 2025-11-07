package school.faang.user_service.controller.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.LinkedHashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class ErrorResponseFactory {

    private ErrorResponseFactory() {
    }

    public static ErrorResponse create(Exception e, HttpServletRequest req, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(req))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(safeMessage(e, status))
                .build();
    }

    public static ErrorResponse create(MethodArgumentNotValidException e, HttpServletRequest req, HttpStatus status) {
        Map<String, List<String>> errors = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                fe -> fe.getDefaultMessage() != null && !fe.getDefaultMessage().isBlank()
                                        ? fe.getDefaultMessage() : "Unknown error",
                                Collectors.toList()
                        )
                ));

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(req))
                .status(status.value())
                .error(status.getReasonPhrase())
                .details(errors)
                .message("Validation failed")
                .build();
    }

    private static String safeMessage(Throwable e, HttpStatus status) {
        if (status.is5xxServerError()) {
            return "Internal server error";
        }

        return Optional.ofNullable(e.getMessage())
                .filter(s -> !s.isBlank())
                .orElse("Unknown error");
    }

    private static String safeUrl(HttpServletRequest req) {
        return Optional.ofNullable(req)
                .map(r -> {
                    String uri = r.getRequestURI();
                    String queryString = r.getQueryString();
                    return queryString == null ? uri : uri + "?" + queryString;
                })
                .orElse("N/A");
    }
}