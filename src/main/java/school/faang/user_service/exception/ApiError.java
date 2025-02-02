package school.faang.user_service.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApiError {

  private HttpStatus status;
  private String message;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, String> errors;

  private LocalDateTime timestamp = LocalDateTime.now();

  public ApiError(HttpStatus status, String message, Map<String, String> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }
}
