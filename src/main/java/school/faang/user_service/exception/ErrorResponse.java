package school.faang.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private String errorDescription;
    private String Description;

    public ErrorResponse(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
