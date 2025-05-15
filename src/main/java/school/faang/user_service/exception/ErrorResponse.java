package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    @JsonProperty(value = "errorDate")
    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd hh:mm:ss",
            without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
    )
    private final LocalDateTime errorDate;

    @JsonProperty(value = "errorMessage")
    private final String errorMessage;

    public ErrorResponse(String errorMessage) {
        this.errorDate = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
}
