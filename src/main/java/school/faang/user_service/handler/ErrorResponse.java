package school.faang.user_service.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
}