package school.faang.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
public class ExceptionDto {
    @NotNull(message = "statusCode mustn't be null")
    private int statusCode;
    private HttpStatus status;

    @NotNull(message = "Description mustn't be null")
    @NotBlank(message = "Description mustn't be blank")
    @Size(max = 4096, message = "Description mustn't exceed 4096 characters")
    private String description;

    @NotNull(message = "The error message can't be null")
    @NotBlank(message = "The error message shouldn't be blank")
    private List<String> errorMessages;
}
