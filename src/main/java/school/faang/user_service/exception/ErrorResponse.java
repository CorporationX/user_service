package school.faang.user_service.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Сообщение об ошибки")
    private String message;
}